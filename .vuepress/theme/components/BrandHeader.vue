<template>
  <section id="brand-header">
    <div class="theme-container">
      <div class="navbar-brand wrap">
        <a class="navbar-item logo" href="https://www.emqx.com/zh">
          <img alt="5G时代，开源物联网MQTT服务器" loading="lazy" src="https://docs.emqx.cn/logo.png" />
        </a>
        <ClientOnly>
          <div class="navbar-item has-dropdown is-hoverable">
            <a class="navbar-link">{{ $lang === 'zh-CN' ? '旗下产品' : 'Products' }}</a>
            <div class="navbar-dropdown moblie">
              <a
                class="navbar-item"
                target="_blank"
                :href="
                  $lang === 'zh-CN' ? 'https://www.emqx.com/zh/products/emqx' : 'https://www.emqx.com/en/products/emqx'
                "
              >
                EMQ X Broker
              </a>
              <a
                class="navbar-item"
                target="_blank"
                :href="
                  $lang === 'zh-CN' ? 'https://www.emqx.com/zh/products/emqx' : 'https://www.emqx.com/en/products/emqx'
                "
              >
                EMQ X Enterprise
              </a>
              <a class="navbar-item" target="_blank" href="https://hstream.io/"> HStream </a>
              <a
                class="navbar-item"
                target="_blank"
                :href="
                  $lang === 'zh-CN'
                    ? 'https://www.emqx.com/zh/products/neuron'
                    : 'https://www.emqx.com/en/products/neuron'
                "
              >
                Neuron
              </a>
              <a class="navbar-item" target="_blank" href="https://github.com/lf-edge/ekuiper"> Kuiper </a>
            </div>
            <div class="navbar-dropdown desktop">
              <div class="columns">
                <div class="nav-list column">
                  <div class="columns is-gapless">
                    <div class="column">
                      <h1>Message Broker</h1>
                      <div class="nav-contents for-cloud">
                        <a
                          target="_blank"
                          :href="
                            $lang === 'zh-CN'
                              ? 'https://www.emqx.com/zh/products/emqx'
                              : 'https://www.emqx.com/en/products/emqx'
                          "
                        >
                          <div class="nav-item">
                            <div class="navbar-icon icon-broker"></div>
                            <span>EMQ X Broker</span>
                          </div>
                        </a>
                        <a
                          target="_blank"
                          :href="
                            $lang === 'zh-CN'
                              ? 'https://www.emqx.com/zh/products/emqx'
                              : 'https://www.emqx.com/en/products/emqx'
                          "
                        >
                          <div class="nav-item">
                            <div class="navbar-icon icon-enterprise"></div>
                            <span>EMQ X Enterprise</span>
                          </div>
                        </a>
                      </div>
                      <div class="nav-contents for-edge">
                        <a
                          target="_blank"
                          :href="
                            $lang === 'zh-CN'
                              ? 'https://www.emqx.com/zh/products/neuron'
                              : 'https://www.emqx.com/en/products/neuron'
                          "
                        >
                          <div class="nav-item">
                            <div class="navbar-icon icon-neuron"></div>
                            <span>Neuron</span>
                          </div>
                        </a>
                      </div>
                    </div>
                    <div class="column">
                      <h1>Stream Processing</h1>
                      <div class="nav-contents">
                        <a target="_blank" href="https://hstream.io/">
                          <div class="nav-item">
                            <div class="navbar-icon icon-hstream"></div>
                            <span>HStream</span>
                          </div>
                        </a>
                      </div>
                      <div class="nav-contents">
                        <a
                          target="_blank"
                          :href="
                            $lang === 'zh-CN'
                              ? 'https://github.com/lf-edge/ekuiper'
                              : 'https://github.com/lf-edge/ekuiper'
                          "
                        >
                          <div class="nav-item">
                            <div class="navbar-icon icon-kuiper"></div>
                            <span>Kuiper</span>
                          </div>
                        </a>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="column">
                  <a target="_blank" :href="$lang === 'zh-CN' ? 'https://cloud.emqx.cn/' : 'https://cloud.emqx.io/'">
                    <div class="brand-cloud">
                      <h2>
                        {{
                          $lang === 'zh-CN'
                            ? 'EMQ X Cloud - 全托管的 MQTT 云服务'
                            : 'EMQ X Cloud - Fully Managed MQTT IoT Cloud'
                        }}
                      </h2>
                      <span> {{ $lang === 'zh-CN' ? '180天免费试用' : 'Start a 180-day free trial' }} → </span>
                    </div>
                  </a>
                </div>
              </div>
            </div>
          </div>
        </ClientOnly>
        <a
          class="navbar-item"
          target="_blank"
          :href="$lang === 'zh-CN' ? 'https://www.emqx.com/zh/about' : 'https://www.emqx.com/en/about'"
        >
          {{ $lang === 'zh-CN' ? '关于我们' : 'About' }}
        </a>
      </div>
    </div>
  </section>
</template>

<script>
export default {}
</script>

<style lang="stylus">
$brand-cloud = '/images/brand-header/brand-cloud.png'
$navbar-icon-hstream = '/images/brand-header/navbar-icon-hstream.png'
$navbar-icon-kuiper = '/images/brand-header/navbar-icon-kuiper.png'
$navbar-icon-neuron = '/images/brand-header/navbar-icon-neuron.png'
$navbar-icon-broker = '/images/brand-header/navbar-icon-broker.png'
$navbar-icon-enterprise = '/images/brand-header/navbar-icon-enterprise.png'

#brand-header {
  background-color: #fff;
  border-bottom: 1px solid #e3e5ec;
  .navbar-brand {
    display: flex;
    align-items: center;
    flex-shrink: 0;
    min-height: 4.5rem;
  }
  .navbar-link,
  .navbar-item {
    display: flex;
    align-items: center;
    position: relative;
    padding: 0.5rem 0.75rem;
    font-size: 14px;
    font-weight: 500;
    color: #5e5e5e;
  }
  .navbar-link {
    cursor pointer
    padding-right: 2.5em;
    &::after {
      border: 2px solid #4d4d4d;
      border-radius: 2px;
      border-right: 0;
      border-top: 0;
      content: ' ';
      display: block;
      width: 0.375em;
      height: 0.375em;
      margin-top: -0.4375em;
      pointer-events: none;
      position: absolute;
      top: 50%;
      margin-top: -0.375em;
      right: 1.25em;
      transform: rotate(-45deg);
      transform-origin: center;
    }
  }
  .navbar-item {
    &.logo {
      padding-left: 0;
      margin-right: 1rem;
      > img {
        height: 22px;
      }
    }
    &.is-hoverable:hover .navbar-dropdown {
      &.moblie {
        display: block;
      }
      @media screen and (min-width: 767px) {
        &.desktop {
          opacity: 1;
          pointer-events: auto;
          transform: rotateX(0);
        }
        &.moblie {
          display: none;
        }
      }
    }
    &.has-dropdown {
      padding: 0;
    }
  }
  .navbar-dropdown {
    padding-bottom: 0.5rem;
    padding-top: 0.5rem;
    background-color: #fff;
    border-radius: 6px;
    border: 1px solid #eef0f1;
    display: none;
    font-size: 0.875rem;
    left: 0;
    min-width: 100%;
    position: absolute;
    top: 100%;
    z-index: 1001;
    a.navbar-item {
      font-weight: 400;
      &:hover {
        background-color: #f5f5f5;
        color: #0a0a0a;
      }
    }
    &.desktop {
      @media screen and (min-width: 767px) {
        display: block;
      }
      display: none;
      padding: 24px 36px;
      opacity: 0;
      pointer-events: none;
      transform-origin: 50% -16px;
      transform: rotateX(45deg);
      transition: all 250ms;
      transition-property: transform, width, height;
      box-shadow: 0 50px 100px -20px rgba(50, 50, 93, 0.25), 0 30px 60px -30px rgba(0, 0, 0, 0.3);
      will-change: transform, width, height;
      &::before {
        content: '';
        position: absolute;
        top: -4px;
        left: 40px;
        background-color: #fff;
        transform: rotate(45deg);
        transition-property: transform;
        transition-duration: 250ms;
        box-shadow: -2px -2px 0 -1px rgba(50, 50, 93, 0.1);
        margin: 0 0 0 -6px;
        width: 12px;
        height: 12px;
        border-radius: 3px 0 0 0;
      }
      .brand-cloud {
        margin: 0 auto;
        height: 100%;
        min-width: 400px;
        text-align: center;
        font-size: 16px;
        font-weight: 500;
        background: #fafbfc url($brand-cloud) no-repeat top/contain;
        background-size: 380px;
        h2 {
          padding-top: 150px;
          font-size: 16px;
          font-weight: 500;
          color: #4d4d4d;
          border-bottom none
        }
        span {
          color: #00b173;
        }
      }
      .nav-list {
        width: 480px;
        h1 {
          font-size: 14px;
          font-weight: 600;
          color: #959595;
          margin-bottom: 32px;
        }
        .nav-contents {
          padding: 20px 0 32px;
          position: relative;
          &:nth-of-type(1) {
            height: 52px;
          }
          &[class*='for-']::before {
            position: absolute;
            top: -6.5px;
            background-color: #fff;
            font-size: 12px;
            line-height: 1;
            font-weight: 400;
            color: #959595;
          }
          &.for-cloud::before {
            content: 'for Cloud';
          }
          &.for-edge::before {
            content: 'for Edge';
          }
          border-top: 1px solid #eef0f1;
          > a:not(:last-child) {
            .nav-item {
              margin-bottom: 12px;
            }
          }
          .nav-item {
            display: flex;
            .navbar-icon {
              width: 20px;
              height: 20px;
              margin-right: 8px;
              background-position: center;
              background-repeat: no-repeat;
              background-size: cover;
              &.icon-broker {
                background-image: url($navbar-icon-broker);
              }
              &.icon-enterprise {
                background-image: url($navbar-icon-enterprise);
              }
              &.icon-hstream {
                background-image: url($navbar-icon-hstream);
              }
              &.icon-neuron {
                background-image: url($navbar-icon-neuron);
              }
              &.icon-kuiper {
                background-image: url($navbar-icon-kuiper);
              }
            }
            span {
              font-size: 14px;
              font-weight: 500;
              color: #1d1d1d;
            }
          }
        }
      }
    }
  }
}
@media screen and (max-width: 767px) {
  #brand-header {
    display none
  }
}
</style>
