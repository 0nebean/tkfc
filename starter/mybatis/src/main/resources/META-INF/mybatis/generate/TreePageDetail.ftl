<template>
  <Drawer v-model="show" :title="drawerTitle" :width="store.useViewSizeStore().drawerWidthVal" :mask-closable="false" :styles="styles" @on-visible-change="resetAddForm">
    <Spin fix :show="detailLoading"/>
    <Row>
      <Col :span="store.useViewSizeStore().drawerSpanVal">
      <Form ref="addDataFrom" shadow label-position="left" :model="addDataFrom" :rules="validateRules">
        <Row v-if="entityId !== 1" justify="start">
          <Col span="24">
          <FormItem label="上级 :" prop="parentId">
            <TreeSelect v-model.number="addDataFrom.parentId" :data="deptTreeList" placeholder="请选择上级" :disabled="readOnly" />
          </FormItem>
          </Col>
        </Row>

        <Row justify="start">
          <Col span="24">
          <FormItem label="名称 :" prop="chName">
            <Input v-model.trim="addDataFrom.chName" type="text" placeholder="请输入名称" :disabled="readOnly"></Input>
          </FormItem>
          </Col>
        </Row>

    <#if fieldArr?exists>
      <#list fieldArr as item>
        <#if item.columnName != 'parentId' && item.columnName != 'chName'
        && item.columnName != 'sort' && item.columnName != 'operatorId'
        && item.columnName != 'operatorName' && item.columnName != 'isDeleted'
        && item.columnName != 'createTime' && item.columnName != 'updateTime'>
        <Row justify="start">
          <Col span="24">
          <FormItem label="${item.comment} :" prop="${item.columnName}">
          <#if item.isEnum>
            <DictSelector v-model.trim="addDataFrom.${item.columnName}" placeholder="请选择${item.comment}" ref="${item.columnName}Selector" groupVal="${item.enumGroupVal}" :disabled="readOnly" />
          <#else>
            <Input v-model.trim="addDataFrom.${item.columnName}" type="text" placeholder="请输入${item.comment}" :disabled="readOnly"></Input>
          </#if>
          </FormItem>
          </Col>
        </Row>

        </#if>
      </#list>
    </#if>

        <Row justify="start">
          <Col span="24">
          <FormItem label="排序 :" prop="sort">
            <Input v-model.number="addDataFrom.sort" type="number" placeholder="请输入排序值" :disabled="readOnly"></Input>
          </FormItem>
          </Col>
        </Row>
      </Form>

      <template v-hasPrem="['${permShortName}_SAVE']">
        <div v-if="!readOnly" class="demo-drawer-footer mt-20 ml-10">
          <Button type="primary" :disabled="disableSubmit" @click="submitAddFrom('addDataFrom')">提交</Button>
        </div>
      </template>

      </Col>
    </Row>
  </Drawer>
</template>

<script>
import ${modelVarName}Api from "./${modelName}Api"
import DictSelector from "@/view/common/selector/from/DictSelector.vue"

  export default {
    components: {
      DictSelector,
    },
    inject: ["bin", "api", "http", "tips", "store"],
    props: {
      reloadListData: {
        type: Function,
        default: () => {}
      },
      showDetail: {
        type: Boolean,
        default: () => false
      }
    },
    emits: ["closeDetail"],
    data() {
      return {
        title: "${description}",
        styles: {
          height: "calc(100% - 55px)",
          overflow: "auto",
          paddingBottom: "53px",
          position: "static"
        },
        disableSubmit: false,
        readOnly: false,
        entityId: 0,
        addDataFrom: {},
        deptTreeList: [],
        detailLoading: false,
        validateRules: {
          parentId: [
            {
              required: true,
              message: "请选择上级",
              type: "number",
              trigger: "change"
            }
          ],
          chName: [
            {
              required: true,
              message: "请输入名称",
              trigger: "blur"
            },
            {
              message: "该字段长度只能在[2-50]之间",
              min: 2,
              max: 50,
              trigger: "blur"
            }
          ],
          <#if fieldArr?exists>
          <#list fieldArr as item>
            <#if item.columnName != 'parentId' && item.columnName != 'chName'
            && item.columnName != 'sort' && item.columnName != 'operatorId'
            && item.columnName != 'operatorName' && item.columnName != 'isDeleted'
            && item.columnName != 'createTime' && item.columnName != 'updateTime'>
          ${item.columnName}: [
            {
              required: true,
              <#if item.isEnum>
              message: "请选择${item.comment}",
              trigger: "change"
              <#else>
              message: "请输入${item.comment}",
              trigger: "blur"
              </#if>
            }
          ],
            </#if>
          </#list>
          </#if>
          sort: [
            {
              required: true,
              type: "integer",
              min: 0,
              max: 9999,
              message: "排序值只能在[0-9999]之间的",
              trigger: "blur"
            }
          ]
        }
      }
    },
    computed: {
      drawerTitle() {
        if (this.entityId === 0) {
          return `新增${r"${this.title}"}`
        }
        return this.readOnly === true ? `查看${r"${this.title}"}` : `编辑${r"${this.title}"}`
      },
      show: {
        // getter
        get() {
          return this.showDetail
        },
        // setter
        set(newValue) {
          // 注意：我们这里使用的是解构赋值语法
          this.$emit("closeDetail", newValue)
        }
      }
    },
    mounted() {
      this.disableSubmit = false
      <#if fieldArr?exists>
      this.$nextTick(() => {
        const refs = [
          <#list fieldArr as item>
          <#if item.isEnum>
          "${item.columnName}Selector",
          </#if>
          </#list>
        ]
        this.bin.loadNext(refs, 0, this.$refs)
      })
      </#if>
    },
    methods: {
      submitAddFrom(name) {
        this.$refs[name].validate(valid => {
          if (valid) {
            this.disableSubmit = true
            this.http.request(
                ${modelVarName}Api.save(this.addDataFrom),
                () => {
                  this.tips.successMsg("保存成功")
                  this.reloadListData()
                  setTimeout(() => {
                    this.disableSubmit = false
                    this.show = false
                  }, 300)
                },
                () => {
                  this.disableSubmit = false
                }
            )
          } else {
            this.tips.errMsg("请完善表单信息!")
          }
        })
      },
      loadParentNode(callback) {
        this.http.request(${modelVarName}Api.syncTreeData({ selfId: this.entityId }), resp => {
          this.deptTreeList = resp.data
          if (this.bin.nonNull(callback)) {
            callback()
          }
        })
      },
      initDetailData(entityId, readOnly) {
        this.$emit("closeDetail", true)
        this.entityId = entityId
        this.readOnly = readOnly
        this.detailLoading = true
        this.loadParentNode(() => {
          if (entityId !== 0) {
            this.http.request(
                ${modelVarName}Api.get(entityId),
                resp => {
                  this.addDataFrom = resp.data
                  this.detailLoading = false
                },
                () => {
                  this.detailLoading = false
                }
            )
          } else {
            this.detailLoading = false
          }
        })
      },
      resetAddForm(status) {
        if (!status) {
          this.$refs.addDataFrom.resetFields()
          this.addDataFrom = {}
          this.readOnly = false
        }
      }
    }
  }
</script>

<style></style>
