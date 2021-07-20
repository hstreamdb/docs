<template>
  <div id="loader-wrapper">
    <div class="loader">
      <div class="ball-spin-fade-loader">
        <div v-for="item in 8" :key="`out${item}`"></div>
      </div>
    </div>
  </div>
</template>

<style lang="stylus" scoped>
#loader-wrapper
  height: calc(100vh - 72px)
  background #fff
  background var(--background-color)
  .loader
    position: relative
    width:120px
    height:120px
    top:calc(50% - 60px)
    left:50%
    z-index:5
    transform:translate(-50%,-50%)
    .ball-spin-fade-loader
      position: relative
      top: 50px
      left: 50px
      > div
        background-color:#613EDA
        margin: 2px
        animation-fill-mode: both
        position: absolute
        width: 15px
        height: 15px
        border-radius: 100%
        &:nth-child(1){ animation: ball-spin-fade-loader 1s -.96s infinite linear }
        &:nth-child(2){ animation: ball-spin-fade-loader 1s -.84s infinite linear }
        &:nth-child(3){ animation: ball-spin-fade-loader 1s -.72s infinite linear }
        &:nth-child(4){ animation: ball-spin-fade-loader 1s -.6s infinite linear }
        &:nth-child(5){ animation: ball-spin-fade-loader 1s -.48s infinite linear }
        &:nth-child(6){ animation: ball-spin-fade-loader 1s -.36s infinite linear }
        &:nth-child(7){ animation: ball-spin-fade-loader 1s -.24s infinite linear }
        &:nth-child(8){ animation: ball-spin-fade-loader 1s -.12s infinite linear }
        &:nth-child(1)
          top: 25px
          left: 0
        &:nth-child(2)
          top: 17.05px
          left: 17.05px
        &:nth-child(3)
          top: 0
          left: 25px
        &:nth-child(4)
          top: -17.05px
          left: 17.05px
        &:nth-child(5)
          top: -25px
          left: 0
        &:nth-child(6)
          top: -17.05px
          left: -17.05px
        &:nth-child(7)
          top: 0
          left: -25px
        &:nth-child(8)
          top: 17.05px
          left: -17.05px
        @keyframes ball-spin-fade-loader
          50% {
            opacity: .3
            -webkit-transform: scale(.4)
            transform: scale(.4)
          }
          100% {
            opacity: 1
            -webkit-transform: scale(1)
            transform: scale(1)
          }
</style>
