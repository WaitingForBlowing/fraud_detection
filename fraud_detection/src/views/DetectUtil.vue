<template>
  <div class="container">
    <el-row :gutter=200>
      <el-col :span=7 :offset=4>
        <el-card :body-style="{ padding: '0px', height: '300px'}" shadow="hover" class="detect-option">
          <i class="detect-icon el-icon-circle-plus-outline" @click="showDetectSingle"></i>
          <span class="detect-text">原始数据检测</span>
        </el-card>
      </el-col>
      <el-col :span=7>
        <el-card :body-style="{ padding: '0px', height: '300px' }" shadow="hover" class="detect-option">
          <i class="detect-icon el-icon-folder-add" @click="showDetectMulti"></i>
          <span class="detect-text">财务比例检测</span>
        </el-card>
      </el-col>
    </el-row>
    <!--单个检测对话框-->
    <el-dialog
        title="原始数据检测"
        :visible.sync="singleDialogVisible"
        :close-on-click-modal="false"
        @close="singleDialogClosed">

      <el-form ref="singleDetectForm" :model="detectForm" label-width="80px">
        <el-form-item label="模型选择">
          <el-select v-model="detectForm.modelType" placeholder="请选择">
            <el-option
                v-for="item in modelList"
                :key="item.id"
                :label="item.name"
                :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="上传数据">
          <el-upload
              action="#"
              :limit="1"
              :on-change="uploadChange"
              :file-list="fileList"
              :auto-upload="false">
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">
              <span>格式请参考模板文件</span>
              <el-link type="primary" :underline="false" :href="singleDemoUrl">raw_demo.csv</el-link>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="singleDialogClosed">取 消</el-button>
        <el-button type="primary" @click="singleDetect('singleDetectForm')">确 定</el-button>
      </div>
    </el-dialog>
    <!--批量检测对话框-->
    <el-dialog
        title="财务比例检测"
        :visible.sync="multiDialogVisible"
        :close-on-click-modal="false"
        @close="multiDialogClosed">
      <el-form ref="multiDetectForm" :model="detectForm" label-width="80px">
        <el-form-item label="模型选择">
          <el-select v-model="detectForm.modelType" placeholder="请选择">
            <el-option
                v-for="item in modelList"
                :key="item.id"
                :label="item.name"
                :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="上传数据">
          <el-upload
              action="#"
              :limit="1"
              :on-change="uploadChange"
              :file-list="fileList"
              :auto-upload="false">
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">
              <span>格式请参考模板文件</span>
              <el-link type="primary" :underline="false" :href="multiDemoUrl">demo.xlsx</el-link>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="multiDialogClosed">取 消</el-button>
        <el-button type="primary" @click="multiDetect('multiDetectForm')">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {getAllModel} from "@/api/model";
import {multiDetect, singleDetect} from "@/api/detect";

export default {
  name: "DetectUtil",
  data() {
    return {
      singleDialogVisible: false,
      multiDialogVisible: false,
      detectForm: {
        modelType: 1,
        file: null
      },
      modelList: [],
      fileList: [],
      singleDemoUrl: "http://127.0.0.1:9000/online/raw_demo.csv?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=QD99MYPGJPH5P1KGF8PD%2F20230508%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20230508T070100Z&X-Amz-Expires=604800&X-Amz-Security-Token=eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhY2Nlc3NLZXkiOiJRRDk5TVlQR0pQSDVQMUtHRjhQRCIsImV4cCI6MTY4MzU3MTcxNCwicGFyZW50IjoibWluaW9hZG1pbiJ9.PvIKVlU1nxrznadiVbMsLtNFXC8W7Kh3wKqJfsII9emJeErO4KEnCLO4-sl6oYXalLzmKzom6mUV0K0yoxo0Tw&X-Amz-SignedHeaders=host&versionId=null&X-Amz-Signature=e5442ceb164ed31adad0de1e94bdad2c9419554a63dd34b9288fd948845f759b",
      multiDemoUrl: ""
    }
  },
  methods: {
    async showDetectSingle() {
      const {data} = await getAllModel()
      this.modelList = data
      this.singleDialogVisible = true
    },
    singleDialogClosed() {
      this.singleDialogVisible = false
    },
    showDetectMulti() {
      this.multiDialogVisible = true
    },
    multiDialogClosed() {
      this.multiDialogVisible = false
    },
    async singleDetect(formName) {
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          const {code} = await singleDetect(this.detectForm)
          if (code === 200) {
            this.$notify.success({
              title: '成功',
              message: '查看结果请通过历史记录查询',
            })
            this.singleDialogVisible = false
          } else {
            this.$notify.error({
              title: '失败',
              message: message,
            })
          }

        } else {
          return false
        }
      })
    },
    async multiDetect(formName) {
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          const {code} = await multiDetect(this.detectForm)
          this.multiDialogVisible = false
        } else {
          return false
        }
      })
    },
    uploadChange(file, fileList) {
      console.log(file.name)
      this.detectForm.file = fileList[0].raw;
    },
  }
}
</script>

<style scoped>
.container {
  padding-top: 250px;
  padding-left: 300px;
}

.detect-icon {
  display: block;
  height: 200px;
  width: 200px;
  font-size: 200px;
  margin-left: 60px;
  margin-top: 20px;
}

.detect-text {
  display: block;
  font-size: 30px;
  width: 100%;
  text-align: center;
  margin-top: 25px;
}

.detect-option {
  cursor: pointer;
}

</style>
