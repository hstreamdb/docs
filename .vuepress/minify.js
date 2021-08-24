const fs = require('fs')
const path = require('path')
const minify = require('html-minifier').minify

const sourceDir = path.join(__dirname, './dist')
const showCompress = true
const minifyJS = showCompress
  ? {
      compress: {
        warnings: false,
        drop_debugger: true,
        drop_console: true,
      },
    }
  : true //配置压缩js,showCompress为true时压缩代码并去除console,debugger控制台提示，正式发布上线可开启，否则只压缩js

fileDisplay(sourceDir)

/**
 * 文件遍历方法
 * @param filePath 需要遍历的文件路径
 */
function fileDisplay(filePath) {
  //根据文件路径读取文件，返回文件列表
  fs.readdir(filePath, function (err, files) {
    if (err) {
      console.warn(err)
    } else {
      //遍历读取到的文件列表
      files.forEach(function (filename) {
        //获取当前文件的绝对路径
        var filedir = path.join(filePath, filename)
        //根据文件路径获取文件信息，返回一个fs.Stats对象
        fs.stat(filedir, function (eror, stats) {
          if (eror) {
            console.warn('获取文件stats失败')
          } else {
            var isFile = stats.isFile() //是文件
            var isDir = stats.isDirectory() //是文件夹
            if (isFile && /\.htm/.test(filedir)) {
              //压缩.htm或.html文件
              miniHtml(filedir)
            }
            if (isDir) {
              fileDisplay(filedir) //递归，如果是文件夹，就继续遍历该文件夹下面的文件
            }
          }
        })
      })
    }
  })
}

function miniHtml(filedir) {
  fs.readFile(filedir, 'utf8', function (err, data) {
    if (err) {
      throw err
    }
    fs.writeFile(
      filedir,
      minify(data, {
        //主要压缩配置
        processScripts: ['text/html'],
        collapseWhitespace: true,
        minifyJS: minifyJS,
        minifyCSS: true,
        removeComments: true, //删除注释
        removeCommentsFromCDATA: true, //从脚本和样式删除的注释
      }),
      function () {
        console.log(filedir, 'success')
      },
    )
  })
}
