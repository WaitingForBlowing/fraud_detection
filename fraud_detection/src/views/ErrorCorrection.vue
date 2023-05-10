<template>
  <div class="container">
    <div class="main">
      <el-card class="box-card">
        <el-form class="query-form" :model="queryInfo.query" :inline="true" size="small" ref="queryForm">
          <el-form-item label="公司名称" prop="firm">
              <el-input v-model="queryInfo.query.firm"></el-input>
          </el-form-item>
          <el-form-item label="财报时间" prop="period">
            <el-input v-model="queryInfo.query.period"></el-input>
          </el-form-item>
          <el-form-item label="检测类型" prop="dataType">
            <el-select v-model="queryInfo.query.dataType" placeholder="请选择">
              <el-option
                  v-for="item in dataTypeList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="模型类型" prop="modelType">
            <el-select v-model="queryInfo.query.modelType" placeholder="请选择">
              <el-option
                  v-for="item in modelList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="造假情况" prop="result">
            <el-select v-model="queryInfo.query.result" placeholder="请选择">
              <el-option
                  v-for="item in resultEnum"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <br/>
          <el-form-item class="submit-btn">
            <el-button type="primary" @click="getResultList">查询</el-button>
          </el-form-item>
          <el-form-item>
            <el-button @click="resetForm()">重置</el-button>
          </el-form-item>
        </el-form>
        <!--表格区域-->
        <el-table
            :data="resultList"
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
              prop="firm"
              label="公司名称"
              :resizable="false"/>
          <el-table-column
              prop="period"
              label="财报日期"
              :resizable="false"/>
          <el-table-column
              prop="modelType"
              label="模型类型"
              :resizable="false"/>
          <el-table-column
              prop="dataType"
              label="检测类型"
              :resizable="false">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.dataType === 0" effect="dark">原始数据</el-tag>
              <el-tag v-if="scope.row.dataType === 1" type="success" effect="dark">财务比率</el-tag>
            </template>
          </el-table-column>
          <el-table-column
              prop="result"
              label="检测结果"
              :resizable="false">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.result === 0" effect="dark" type="success">无造假</el-tag>
              <el-tag v-if="scope.row.result === 1" effect="dark">疑似造假</el-tag>
            </template>
          </el-table-column>
          <el-table-column
              label="纠正准确性"
              :resizable="false">
            <template slot-scope="scope">
              <!-- 修改 -->
              <el-popconfirm
                  title="确定需要更正吗?"
                  @confirm="setAccuracy(scope.row.id)">
                <el-button type="primary" size="mini" icon="el-icon-edit" slot="reference"></el-button>
              </el-popconfirm>

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
import {getAllModel} from "@/api/model";
import {getResultList, updateResult} from "@/api/result";


export default {
  name: "ErrorCorrection",
  data() {
    return {
      queryInfo: {
        query: {
          firm: '',
          period: '',
          modelType: '',
          dataType: '',
          result: ''
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
      resultEnum: [
        {
          id: 0,
          name: '未作弊'
        },
        {
          id: 1,
          name: '疑似造假',
        }
      ],
      resultList:[],
      total: 0
    }
  },
  created() {
    this.getAllModel()
    this.getResultList()
  },
  methods: {
    async getResultList() {
      let query = JSON.stringify(this.queryInfo.query)
      let queryParam = {
        query: query,
        pageNumber: this.queryInfo.pageNumber,
        pageSize: this.queryInfo.pageSize
      }
      const {data} = await getResultList(queryParam)
      this.resultList = data.records.map(r => {
        const dateObj = new Date(r.uploadTime);
        const formattedDate = dateObj.toLocaleDateString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        });
        return {
          id: r.id,
          firm: r.firm,
          period: r.period,
          uploadTime: formattedDate,
          modelType: this.modelList.find(model => model.id === r.modelType).name,
          dataType: r.dataType,
          result: r.result,
        }
      })
      this.total = data.total
    },
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.getResultList()
    },
    handleCurrentChange(newPage) {
      this.queryInfo.pageNumber = newPage
      this.getResultList()
    },
    resetForm() {
      this.$refs['queryForm'].resetFields()
      this.getResultList()
    },
    async getAllModel() {
      const {data} = await getAllModel()
      this.modelList = data
    },
    async setAccuracy(id) {
      const {code} = await updateResult(id)
      if (code === 200) {
        await this.getResultList()
      }
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

.header .bread-crumb {
  font-size: 14px;
  font-family: Avenir, 'Helvetica Neue', Arial, Helvetica, sans-serif;
  height: 21px;
  line-height: 21px;
}

.header .user-manage-title {
  display: block;
  float: left;
  font-size: 20px;
  color: rgba(0, 0, 0, .85);
  font-weight: 600;
  margin-top: 8px;
  line-height: 32px;
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

.main .box-card .new-btn {
  width: 82px;
  text-align: center;
}

.main .box-card .new-btn i {
  font-weight: 900 !important;
  margin-right: 10px !important;
}

.main .box-card .submit-btn {
  margin-left: 35px;
}

.query-form {
  flex-wrap: wrap!important;
}
.query-form .el-form-item {
  margin-left: 15px!important;
}
</style>
