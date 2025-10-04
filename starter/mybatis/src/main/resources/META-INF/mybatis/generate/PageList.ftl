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
              <Button type="info" @click="selectAllTableItem()">
                <Tooltip content="全选/反选" :delay="this.store.useViewSizeStore().toolTipsDelayValue">
                  <Icon type="md-checkmark" />
                </Tooltip>
              </Button>
              <template v-hasPrem="['${permShortName}_PAGE']">
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
                <Button type="warning" @click="batchEditItem()">
                  <Tooltip content="编辑" :delay="this.store.useViewSizeStore().toolTipsDelayValue">
                    <Icon type="md-create" />
                  </Tooltip>
                </Button>
              </template>
              <template v-hasPrem="['${permShortName}_DEL']">
                <Button type="error" @click="batchDelItem()">
                  <Tooltip content="批量删除" :delay="this.store.useViewSizeStore().toolTipsDelayValue">
                    <Icon type="md-trash" />
                  </Tooltip>
                </Button>
              </template>
            </ButtonGroup>
            </Col>
          </Row>
          </Col>
        </Row>
      </Form>
      <Table
              ref="dataTable"
              row-key="id"
              size="small"
              :max-height="tableHeight"
              :columns="formColumns"
              :data="formData"
              :indent-size="10"
              border
              class="table-data-form"
              stripe
              @on-selection-change="tableSelectionChange"
              @on-sort-change="tableSortChange"
      >
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

      <Row justify="end" align="middle" class="mt-20">
        <Col>
        <Page
                :total="searchParam.pagination.totalCount"
                :page-size="searchParam.pagination.pageSize"
                :current="searchParam.pagination.currentPage"
                show-elevator
                show-sizer
                @on-change="handlePage"
                @on-page-size-change="handlePageSize"
        />
        </Col>
      </Row>

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
  inject: ["bin", "api", "http", "tips", "store", "tips"],
  data() {
    return {
      title: "${description}",
      selectedStatus: false,
      showDetail: false,
      confirmModalContent: "",
      modifyDataId: 0,
      confirmType: 0,
      confirmModal: false,
      formData: [],
      selectedIds: [],
      searchParamForm: {},
      searchParam: {
        sort: {
          sort: "desc",
          orderBy: "id"
        },
        pagination: {
          totalCount: 0,
          totalPages: 0,
          currentPage: 1,
          pageSize: 10
        },
        expressions: []
      },
      formColumns: [
        { width: 60, type: "selection", align: "center" },
        { title: "编号", sortable: "custom", key: "id", width: 100 },
<#if fieldArr?exists>
  <#list fieldArr as item>
        <#if item.columnName != 'operatorId' && item.columnName != 'operatorName' && item.columnName != 'isDeleted'>
        { title: `${item.comment}`, key: "${item.columnName}", minWidth: 180 },
        </#if>
  </#list>
</#if>
        { title: "操作", slot: "action", width: 195, align: "center" }
      ]
    }
  },
  computed: {
    tableHeight() {
      return `${r"${this.store.useViewSizeStore().calHeight(70)}"}`
    }
  },
  mounted() {
    this.initDataTable()
  },
  methods: {
    closeModal() {
      this.confirmModal = false
    },
    initDataTable() {
      this.bin.buildSearchCondition(this.searchParamForm, condition => {
        this.searchParam.expressions = condition
        const req = ${modelVarName}Api.page(this.searchParam)
        this.http.request(req, resp => {
          this.searchParam.pagination = resp.pagination
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
    selectAllTableItem() {
      this.selectedStatus = !this.selectedStatus
      this.$refs.dataTable.selectAll(this.selectedStatus)
    },
    submitSearchForm() {
      this.initDataTable()
    },
    resetSearchParamForm() {
      this.searchParamForm = {}
      this.searchParam.expressions = []
      this.initDataTable()
    },
    viewTableItem(id) {
      this.$refs.detailView.initDetailData(id, true)
      this.showDetail = true
    },
    batchEditItem() {
      if (this.selectedIds.length > 1) {
        this.tips.warnMsg("一次只能编辑一条数据")
        return
      }
      if (this.selectedIds.length === 0) {
        this.tips.warnMsg("请选择一条数据编辑")
        return
      }
      this.editTableItem(this.selectedIds[0])
    },
    batchDelItem() {
      if (this.selectedIds.length < 1) {
        this.tips.warnMsg("至少择一条数据删除")
        return
      }
      this.confirmType = 1
      this.confirmModalContent = `即将删除数据选中的 [ ${r"${this.selectedIds.length}"} ] 条数据`
      this.confirmModal = true
    },
    editTableItem(id) {
      this.$refs.detailView.initDetailPreData(() => {
        this.$refs.detailView.initDetailData(id, false)
        this.showDetail = true
      })
    },
    deleteTableItem(item) {
      this.confirmType = 0
      this.confirmModalContent = `即将删除数据 [ ${r"${item.id}"} ]`
      this.confirmModal = true
      this.modifyDataId = item.id
    },
    singleDeleteData() {
      this.doDeleteData([this.modifyDataId])
    },
    batchDeleteData() {
      this.doDeleteData(this.selectedIds)
    },
    doDeleteData(selectedIds) {
      this.http.request(${modelVarName}Api.del({ selectedIds }), () => {
        this.confirmModal = false
        this.tips.successMsg("删除成功")
        this.initDataTable()
      })
    },
    showAddView() {
      this.$refs.detailView.initDetailPreData(() => {
        this.$refs.detailView.entityId = 0
        this.showDetail = true
      })
    },
    handleConfirmOk() {
      switch (this.confirmType) {
        case 0:
          this.singleDeleteData()
          this.confirmModal = false
          break
        case 1:
          this.batchDeleteData()
          this.confirmModal = false
          break
        default:
          this.tips.warnMsg("未知的确认模式")
          this.confirmModal = false
          break
      }
    },
    handlePage(value) {
      this.searchParam.pagination.currentPage = value
      this.initDataTable()
    },
    handlePageSize(value) {
      this.searchParam.pagination.pageSize = value
      this.initDataTable()
    },
    tableSortChange(sortSetting) {
      if (sortSetting.order === "normal") {
        this.searchParam.sort.orderBy = "id"
        this.searchParam.sort.sort = "desc"
      } else {
        this.searchParam.sort.orderBy = sortSetting.key
        this.searchParam.sort.sort = sortSetting.order
      }
      this.initDataTable()
    },
    tableSelectionChange(selection) {
      this.selectedIds = []
      selection.forEach(item => {
        this.selectedIds.push(item.id)
      })
    }
  }
}
</script>
