<template>
  <FrameContent>
    <Row>
    <Col span="24">
    <Row justify="start">
      <Col>
      <span class="table-data-form-title">{{ title }}</span>
      </Col>
    </Row>

    <Divider />

    <Modal v-model="confirmModal" :title="confirmModalContent">
      <p>是否确认操作</p>
      <template #footer>
        <Button type="info" @click="closeModal()">取消</Button>
        <Button type="warning" @click="handleConfirmOk()">确定</Button>
      </template>
    </Modal>

    <Form ref="searchParamForm" :model="searchParamForm" :label-width="80">
      <Row justify="start">
        <Col>
        <FormItem label="编号 :">
          <Input id="searchParamForm-id" v-model.trim="searchParamForm.id" class="table-search-input" placeholder="请输入编号搜索" search-param-expression="and^id^eq^" type="text"></Input>
        </FormItem>
        </Col>
      </Row>
      <Row justify="start" align="middle">
        <Col span="24">
        <Row justify="space-between">
          <Col>
          <Space>
            <template v-hasPrem="['${permShortName}_EXPORT']">
              <Button type="primary" @click="exportExcel()">导出表格</Button>
            </template>
          </Space>
          </Col>
          <Col>
          <ButtonGroup>
            <template v-hasPrem="['${permShortName}_PAGE']">
              <Button v-if="!asyncLoadData" type="info" @click="switchLoadDataType()">
                <Tooltip content="异步数据列表" :delay="this.store.useViewSizeStore().toolTipsDelayValue">
                  <Icon type="md-git-branch" />
                </Tooltip>
              </Button>
              <Button v-if="asyncLoadData" type="info" @click="switchLoadDataType()">
                <Tooltip content="同步数据列表" :delay="this.store.useViewSizeStore().toolTipsDelayValue">
                  <Icon type="md-git-compare" />
                </Tooltip>
              </Button>
              <Button class="ivu-btn-cus2" @click="submitSearchForm()">
                <Tooltip content="查询" :delay="this.store.useViewSizeStore().toolTipsDelayValue">
                  <Icon type="md-search" />
                </Tooltip>
              </Button>
              <Button class="ivu-btn-cus1" @click="resetSearchParamForm()">
                <Tooltip content="重置查询条件" :delay="this.store.useViewSizeStore().toolTipsDelayValue">
                  <Icon type="md-refresh" />
                </Tooltip>
              </Button>
            </template>
            <template v-hasPrem="['${permShortName}_SAVE']">
              <Button type="success" @click="showAddView()">
                <Tooltip content="新增" :delay="this.store.useViewSizeStore().toolTipsDelayValue">
                  <Icon type="md-add" />
                </Tooltip>
              </Button>
            </template>
          </ButtonGroup>
          </Col>
        </Row>
        </Col>
      </Row>
    </Form>
    <Table row-key="id" size="small" :load-data="loadAsyncData" :columns="formColumns" :data="formData" :indent-size="10" border class="table-data-form" stripe>
      <template #type="{ row }">
        <Tag v-if="row.permissionType === '页面'" color="primary">{{ row.permissionType }}</Tag>
        <Tag v-if="row.permissionType === '目录'" color="success">{{ row.permissionType }}</Tag>
        <Tag v-if="row.permissionType === '按钮'" color="warning">{{ row.permissionType }}</Tag>
      </template>
      <template #icon="{ row }">
        <Icon v-if="row.icon" :type="row.icon" />
      </template>
      <template #action="{ row }">
        <Row justify="start">
          <template v-hasPrem="['${permShortName}_SAVE']">
            <Col>
            <Button type="text" size="small" icon="md-create" @click="editTableItem(row.id)">编辑</Button>
            </Col>
          </template>
          <template v-hasPrem="['${permShortName}_DEL']">
            <Col>
            <Button type="text" size="small" icon="md-trash" @click="deleteTableItem(row)">删除</Button>
            </Col>
          </template>
        </Row>
      </template>
    </Table>

    <${modelName}DetailDrawer
            ref="detailView"
            :reload-list-data="initDataTable"
            :show-detail="showDetail"
            @close-detail="
          target => {
            showDetail = target
          }
        "
    />
    </Col>
  </Row>
  </FrameContent>
</template>
<script>
import ${modelVarName}Api from "./${modelName}Api"
import ${modelName}DetailDrawer from "./${modelName}DetailDrawer"
import FrameContent from "@/view/common/frame/FrameContent"

export default {
  name: "${modelName}List",
  components: {
    ${modelName}DetailDrawer,
    FrameContent
  },
  inject: ["bin", "api", "http", "tips", "store"],
  data() {
    return {
      title: "${description}",
      asyncLoadData: true,
      showDetail: false,
      confirmModalContent: "",
      modifyDataId: 0,
      confirmModal: false,
      formData: [],
      searchParamForm: {},
      searchParam: {
        expressions: []
      },
      formColumns: [
        { title: `机构名`, key: "chName", minWidth: 180, tree: true },
        <#if fieldArr?exists>
        <#list fieldArr as item>
        <#if item.columnName != 'chName' && item.columnName != 'operatorId' && item.columnName != 'operatorName' && item.columnName != 'isDeleted'>
        { title: `${item.comment}`, key: "${item.columnName}", minWidth: 180 },
        </#if>
        </#list>
        </#if>
        { title: "操作", slot: "action", width: 195, align: "center" }
      ]
    }
  },
  watch: {
    asyncLoadData() {
      this.initDataTable()
    }
  },
  mounted() {
    this.initDataTable()
  },
  methods: {
    switchLoadDataType() {
      this.asyncLoadData = !this.asyncLoadData
    },
    closeModal() {
      this.confirmModal = false
    },
    handleConfirmOk() {
      switch (this.confirmType) {
        case 0:
          this.doDeleteData()
          break
        default:
          this.tips.warnMsg("未知的确认模式")
          break
      }
    },
    loadAsyncData(item, callback) {
      this.http.request(${modelVarName}Api.asyncTreeData({ parentId: item.id }), resp => {
        callback(resp.data)
      })
    },
    initDataTable() {
      this.bin.buildSearchCondition(this.searchParamForm, condition => {
        this.searchParam.expressions = condition
        const req = this.asyncLoadData ? ${modelVarName}Api.asyncTreeData(this.searchParam) : ${modelVarName}Api.syncTreeData(this.searchParam)
        this.http.request(req, resp => {
          this.formData = resp.data
        })
      })
      this.formData = []
    },
    exportExcel() {
      this.bin.buildSearchCondition(this.searchParamForm, condition => {
        this.searchParam.expressions = condition
        this.http.request(${modelVarName}Api.export(this.searchParam), resp => {
          this.http.request(this.api.system.file.download(resp.data, true), excel => {
            this.bin.download(resp.data, excel)
          })
        })
      })
    },
    submitSearchForm() {
      if (this.asyncLoadData === false) {
        this.initDataTable()
      } else {
        this.asyncLoadData = false
      }
    },
    resetSearchParamForm() {
      this.searchParamForm = {}
      this.searchParam.expressions = []
      this.initDataTable()
    },
    viewTableItem(id) {
      this.$refs.detailView.initDetailData(id, true)
    },
    editTableItem(id) {
      this.$refs.detailView.initDetailData(id, false)
    },
    deleteTableItem(item) {
      this.confirmType = 0
      this.confirmModal = true
      this.confirmModalContent = `即将删除数据 [ ${r"${item.id}"} ]`
      this.modifyDataId = item.id
    },
    doDeleteData() {
      this.http.request(${modelVarName}Api.del({ selectedIds: [this.modifyDataId] }), () => {
        this.confirmModal = false
        this.tips.successMsg("删除成功")
        this.initDataTable()
      })
    },
    showAddView() {
      this.$refs.detailView.entityId = 0
      this.$refs.detailView.initDetailData(0, false)
    }
  }
}
</script>
