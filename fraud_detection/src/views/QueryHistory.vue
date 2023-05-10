<template>
  <div class="container">
    <div class="main">
      <el-card class="box-card">
        <el-form class="query-form" :model="queryInfo.query" :inline="true" size="small" ref="queryForm">
          <el-form-item label="检测类型">
            <el-select v-model="queryInfo.query.dataType" placeholder="请选择">
              <el-option
                  v-for="item in dataTypeList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="模型类型">
            <el-select v-model="queryInfo.query.modelType" placeholder="请选择">
              <el-option
                  v-for="item in modelList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryInfo.query.state" placeholder="请选择">
              <el-option
                  v-for="item in stateList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item class="submit-btn">
            <el-button type="primary" @click="getRecordList()">查询</el-button>
          </el-form-item>
          <el-form-item>
            <el-button @click="resetForm()">重置</el-button>
          </el-form-item>
        </el-form>
        <!--表格区域-->
        <el-table
            :data="recordList"
            stripe
            border
            fit
            highlight-current-row
        >
          <el-table-column
              type="index"
              width="50"
              align="center"
              :resizable="false"/>
          <el-table-column
              prop="uploadTime"
              label="上传时间"
              :resizable="false"/>
          <el-table-column
              prop="modelType"
              label="模型类型"
              :resizable="false"/>
          <el-table-column
              label="检测类型"
              :resizable="false">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.dataType === 0" effect="dark">原始数据</el-tag>
              <el-tag v-if="scope.row.dataType === 1" type="success" effect="dark">财务比率</el-tag>
            </template>
          </el-table-column>
          <el-table-column
              label="状态"
              :resizable="false">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.state === 0" type="warning" effect="dark">已提交</el-tag>
              <el-tag v-if="scope.row.state === 1" type="success" effect="dark">检测完成</el-tag>
              <el-tag v-if="scope.row.state === 2" type="info" effect="dark">检测失败</el-tag>
            </template>
          </el-table-column>
          <el-table-column
              label="数据文件"
              :resizable="false">
            <template slot-scope="scope">
              <el-button type="primary" size="mini" icon="el-icon-download"
                         @click="downloadFile(scope.row.dataFile)"></el-button>
            </template>
          </el-table-column>
          <el-table-column
              label="结果文件"
              :resizable="false">
            <template slot-scope="scope">
              <el-button type="primary" size="mini" icon="el-icon-download"
                         @click="downloadFile(scope.row.resultFile)"></el-button>
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
    </div>
  </div>
</template>

<script>
import { getAllModel} from "@/api/model";
import { getRecordList} from "@/api/record";
import {downloadFile} from "@/api/file";

export default {
  name: "QueryHistory",
  data() {
    return {
      queryInfo: {
        query: {
          modelType: '',
          dataType: '',
          state: ''
        },
        pageNumber: 1,
        pageSize: 5
      },
      dataTypeList: [
        {
          id: 0,
          name: '原始数据'
        },
        {
          id: 1,
          name: '财务比率'
        }
      ],
      modelList: [],
      stateList: [
        {
          id: 0,
          name: '已提交'
        },
        {
          id: 1,
          name: '检测完成',
        },
        {
          id: 2,
          name: '检测失败'
        }
      ],
      recordList: [],
      total: 0,
    }
  },
  created() {
    this.getAllModel()
    this.getRecordList()
  },
  methods: {
    async getRecordList() {
      let query = JSON.stringify(this.queryInfo.query)
      let queryParam = {
        query: query,
        pageNumber: this.queryInfo.pageNumber,
        pageSize: this.queryInfo.pageSize
      }
      const {data} = await getRecordList(queryParam)
      this.recordList = data.records.map(r => {
        const dateObj = new Date(r.uploadTime);
        const formattedDate = dateObj.toLocaleDateString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        });
        return {
          uploadTime: formattedDate,
          modelType: this.modelList.find(model => model.id === r.modelType).name,
          dataType: r.dataType,
          dataFile: r.dataFile,
          state: r.state,
          resultFile: r.resultFile,
        }
      })
      this.total = data.total
    },
    resetForm() {
      this.$refs['queryForm'].resetFields()
      this.getRecordList()
    },
    async getAllModel() {
      const {data} = await getAllModel()
      this.modelList = data
    },
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.getRecordList()
    },
    handleCurrentChange(newPage) {
      this.queryInfo.pageNumber = newPage
      this.getRecordList()
    },
    downloadFile(path) {
      downloadFile(path).then(data => {
        // 下载文件
        const url = window.URL.createObjectURL(new Blob([data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', "data.csv");
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      }).catch(error => {
        console.log(error);
      });

    }
  }
}
</script>

<style scoped>
.container {
  height: 100%;
}

.header {
  width: 100%;
  height: 90px;
  background-color: #fff;
  margin-top: 2px;
  padding: 12px 24px 16px 24px;
  box-shadow: 0 1px 4px #00152914;
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

.main .box-card .submit-btn {
  margin-left: 35px;
}
</style>
