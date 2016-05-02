"use strict";

var path = require('path');

module.exports = {
  apkPath: path.resolve(__dirname, "app", "build", "outputs", "apk", "app-server-debug-unaligned.apk"),
  testApkPath: path.resolve(__dirname, "app", "build", "outputs", "apk", "app-server-debug-androidTest-unaligned.apk")
};
