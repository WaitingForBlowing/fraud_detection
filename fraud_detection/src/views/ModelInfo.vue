<template>
  <div class="container">
    <div class="main">
      <el-card class="box-card">
        <el-form class="query-form" :model="queryInfo.query" :inline="true" size="small" ref="queryForm">
          <el-form-item class="import-btn">
            <el-button type="primary" @click="showImportDialog">导入模型</el-button>
          </el-form-item>
        </el-form>
        <!--表格区域-->
        <el-table
            :data="modelList"
            stripe
            border
            fit
            highlight-current-row
        >
          <el-table-column
              type="index"
              align="center"
              :resizable="false"/>
          <el-table-column
              prop="name"
              width="600"
              label="模型名称"
              :resizable="false"/>
          <el-table-column
              label="模型详情"
              :resizable="false">
            <template slot-scope="scope">
              <el-button type="primary" size="mini" icon="el-icon-data-line"
                         @click="showDetailDialog(scope.row.id)"></el-button>
            </template>
          </el-table-column>
          <el-table-column
              label="模型下载"
              :resizable="false">
            <template slot-scope="scope">
              <el-button type="primary" size="mini" icon="el-icon-download"
                         @click="downloadModelFile(scope.row.id)"></el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- 分页器 -->
        <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="queryInfo.pageNumber"
            :page-sizes="[1, 2, 5, 10]"
            :page-size="queryInfo.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total">
        </el-pagination>
      </el-card>
      <!--导入对话框-->
      <el-dialog
          title="导入模型"
          :visible.sync="importDialogVisible"
          :close-on-click-modal="false"
          @close="importDialogClosed">
        <el-form ref="importForm" :model="importForm" label-width="80px" :rules="importFormRules">
          <el-form-item label="模型名称" prop="name">
            <el-input v-model="importForm.name"></el-input>
          </el-form-item>
          <el-form-item label="模型文件">
            <el-upload
                action="#"
                :limit="1"
                :on-change="uploadChange"
                :file-list="fileList"
                :auto-upload="false">
              <el-button size="small" type="primary">点击上传</el-button>
              <div slot="tip" class="el-upload__tip">
                上传joblib框架保存的模型文件
              </div>
            </el-upload>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="importDialogClosed">取 消</el-button>
          <el-button type="primary" @click="importModel('importForm')">确 定</el-button>
        </div>
      </el-dialog>
      <!--模型详情-->
      <el-dialog
          title="模型详情"
          :visible.sync="detailDialogVisible"
          :close-on-click-modal="false"
          @close="detailDialogClosed">
        <el-row>
          <el-col :span="12">
            <h4>训练表现:</h4>
            <el-table
                :data="modelInfo"
                border
                style="width: 360px; margin-top: 45px">
              <el-table-column
                  prop="name"
                  label="评价指标">
              </el-table-column>
              <el-table-column
                  prop="value"
                  label="结果">
              </el-table-column>
            </el-table>
          </el-col>
          <el-col :span="12">
            <h4>执行效果:</h4>
            <div id="graph" style="width: 400px; height: 400px;"></div>
          </el-col>
        </el-row>


      </el-dialog>
    </div>
  </div>
</template>

<script>
import { getModelList, importModel} from "@/api/model";
import {downloadFile} from "@/api/file";

export default {
  name: "ModelInfo",
  data() {
    return {
      queryInfo: {
        pageNumber: 1,
        pageSize: 5
      },
      importForm: {
        name: '',
        file: null
      },
      fileList: [],
      importFormRules: {
        name: [
          { required: true, message: '请输入模型名称', trigger: 'blur' },
        ],
      },
      modelList: [],
      total: 0,
      importDialogVisible: false,
      detailDialogVisible: false,
      modelInfo: []
    }
  },
  created() {
    this.getModelList()
  },
  methods: {
    async getModelList() {
      const {data} = await getModelList(this.queryInfo)
      this.modelList = data.records
      this.total = data.total
    },
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.getModelList()
    },
    handleCurrentChange(newPage) {
      this.queryInfo.pageNumber = newPage
      this.getModelList()
    },
    showImportDialog() {
      this.importDialogVisible = true
    },
    uploadChange(file, fileList) {
      this.importForm.file = fileList[0].raw;
    },
    importDialogClosed() {
      this.importDialogVisible = false
    },
    async importModel(formName) {
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          const {code, message} = await importModel(this.importForm)
          if (code === 200) {
            this.$notify.success({
              title: '成功',
              message: '模型导入成功',
            })
            await this.getModelList()
            this.importDialogVisible = false
          } else {
            this.$notify.error({
              title: '失败',
              message: message,
            })
          }
          this.fileList = []
        } else {
          return false
        }
      })
    },
    downloadModelFile(id) {
      const model = this.modelList.find(model => model.id === id)
      // 向后端发送GET请求获取文件流
      downloadFile(model.url).then(data => {
        // 下载文件
        const url = window.URL.createObjectURL(new Blob([data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', model.url);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      }).catch(error => {
        console.log(error);
      });

    },
    showDetailDialog(id) {
      const model = this.modelList.find(model => model.id === id)
      this.modelInfo = [
        {
          name: 'AUC',
          value: model.auc
        },
        {
          name: 'NDCG@K',
          value: model.ndcg
        }
      ]
      this.detailDialogVisible = true
      this.$nextTick(() => {
        let data = []
        if (id === 1) {
          data = [0.68, 0.678, 0.692, 0.692, 0.693, 0.692, 0.695]
        } else if (id === 2) {
          data = [0.72, 0.74, 0.75, 0.75, 0.753, 0.752, 0.756]
        } else if (id === 3) {
          data = [0.75, 0.752, 0.76, 0.761, 0.761, 0.762, 0.762]
        }
        this.drawChart(data)
      })
    },
    detailDialogClosed() {
      this.detailDialogVisible = false
    },
    drawChart(data) {
      // 基于准备好的dom，初始化echarts实例
      let myChart = this.$echarts.init(document.getElementById('graph'));
      // 绘制图表
      myChart.setOption({
        xAxis: {
          data: ['2022-11', '2022-12', '2023-1', '2023-2', '2023-3', '2023-4', '2023-5']
        },
        yAxis: {
          type: 'value',
          name: 'AUC',
          min: 0.6,
          max: 0.8,
        },
        series: [
          {
            data: data,
            type: 'line',
            smooth: true
          }
        ]
      });
    }
  }
}
</script>

<style scoped>
.container {
  height: 100%;
}

.main {
  padding: 25px;
  height: 100%;
  width: 100%;
}

.main .box-card {
  box-shadow: 0 1px 1px rgba(0, 0, 0, .2);
  border-radius: 0;
}

.main .box-card .query-form {
  display: flex;
  align-items: center;
}

</style>
