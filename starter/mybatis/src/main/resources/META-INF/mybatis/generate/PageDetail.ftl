<template>
  <Drawer v-model="show" :title="drawerTitle" :width="store.useViewSizeStore().drawerWidthVal" :mask-closable="false" :styles="styles" @on-visible-change="resetAddForm">
    <Spin fix :show="detailLoading"/>
    <Row>
      <Col :span="store.useViewSizeStore().drawerSpanVal">
        <Form ref="addDataFrom" shadow label-position="left" :model="addDataFrom" :rules="validateRules">
<#if fieldArr?exists>
  <#list fieldArr as item>
    <#if item.columnName != 'operatorId' && item.columnName != 'operatorName' && item.columnName != 'isDeleted' && item.columnName != 'createTime' && item.columnName != 'updateTime'>
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
    DictSelector
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
      detailLoading: false,
      validateRules: {
<#if fieldArr?exists>
  <#list fieldArr as item>
        <#if item.columnName != 'operatorId' && item.columnName != 'operatorName' && item.columnName != 'isDeleted' && item.columnName != 'createTime' && item.columnName != 'updateTime'>
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
    isEdit() {
      return this.bin.notEmpty(this.addDataFrom.id)
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
    initDetailData(entityId, readOnly) {
      this.$emit("closeDetail", true)
      this.entityId = entityId
      this.readOnly = readOnly
      if (entityId !== 0) {
        this.detailLoading = true
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
