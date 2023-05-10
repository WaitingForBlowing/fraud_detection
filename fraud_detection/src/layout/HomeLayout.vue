<template>
  <el-container class="container">
    <el-aside class="aside" :width=" isCollapse ? '64px' : '256px'">
      <div class="logo">
        <img src="@/assets/image/logo.png" alt="">
        <h1 v-if="!isCollapse">Detection Util</h1>
      </div>
      <el-menu
          class="menu"
          background-color="#001529"
          text-color="#ffffffa6"
          active-text-color="#ffd04b"
          unique-opened
          :collapse="isCollapse"
          :collapse-transition="false"
          router
      >
        <el-menu-item class="submenu" :index="item.path" v-for="item in menuList" :key="item.id">
          <div class="menu-item">
            <i :class="icons[item.id]"></i>
            <span>{{ item.name }}</span>
          </div>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container class="right">
      <el-header class="header">
        <span :class="['in-out', 'iconfont', isCollapse ? 'icon-indent' : 'icon-outdent']" @click="toggleCollapse"></span>
      </el-header>
      <el-main class="main">
        <router-view class="content"></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>

export default {
  name: "HomeLayout",
  data() {
    return {
      menuList: [
        {
          id: 1,
          name: '模型介绍',
          path: 'model'
        },
        {
          id: 2,
          name: '检测工具',
          path: 'detect'
        },
        {
          id: 3,
          name: '历史记录',
          path: 'history'
        },
        {
          id: 4,
          name: '模型纠错',
          path: 'error'
        }
      ],
      icons: {
        '1': 'iconfont icon-setting',
        '2': 'iconfont icon-accountbook',
        '3': 'iconfont icon-cluster',
        '4': 'iconfont icon-radarchart'
      },
      isCollapse: false
    }
  },
  created() {
  },
  methods: {
    toggleCollapse () {
      this.isCollapse = !this.isCollapse
    },
  }
}
</script>

<style scoped>
.container {
  height: 100%;
}

.container .aside {
  background-color: #001529;
  height: 100%;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35)
}

.container .aside .submenu .menu-item {
  height: 100%;
  line-height: 60px;
}
.container .aside .submenu {
  font-size: 18px;
  height: 60px;
}

.container .right .header {
  background-color: #fff;
  height: 64px!important;
  line-height: 64px;
  box-shadow: 0 1px 4px #00152914;
}

.container .right .main {
  background-color: #f0f2f5;
}

.container .right .main .content {
  padding-left: 30px;
  padding-right: 30px;
}

.container .aside .logo {
  height: 64px;
  line-height: 64px;
  margin-left: 15px;
}

.container .aside .logo img {
  display: inline-block;
  width: 32px;
  height: 32px;
  vertical-align: middle;
}

.container .aside .logo h1 {
  display: inline-block;
  margin-left: 12px;
  color: #fff;
  font-size: 20px;
  font-weight: 600;
  vertical-align: middle;
  font-family: Avenir, 'Helvetica Neue', Arial, Helvetica, sans-serif;
}

.iconfont {
  margin-right: 10px;
}

.container .right .header .in-out {
  display: inline-block;
  height: 64px;
  line-height: 64px;
  vertical-align: top;
  cursor: pointer;
  font-size: 24px;
}

.sub-menu:hover i {
  color: #fff;
}
</style>
