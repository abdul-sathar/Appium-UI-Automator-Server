"use strict";
var packageJson = require('./package.json');
var path = require('path');
var gulp = require('gulp');
var boilerplate = require('appium-gulp-plugins').boilerplate.use(gulp);
var fs = require('fs');

function mkdir(path) {
    let dirs = path.split('/');
	let dir = dirs.shift();
	let root = dir + '/';
    try { fs.mkdirSync(root); }
    catch (e) {
        //dir wasn't made, something went wrong
        if(!fs.statSync(root).isDirectory()) throw new Error(e);
    }

    return !dirs.length || mkdir(dirs.join('/'), root);
};

//copy apk from tmp location current repo, apllicable only for Windows OS
gulp.task('copyServerApks',function () {
	if(process.platform==="win32" && packageJson.windowsBuildDir !== undefined){
		let moduleName = packageJson.name;
		let destPath = path.resolve(__dirname, "app", "build");
		let srcPath = path.resolve(packageJson.windowsBuildDir, moduleName, "app", "build");
		var ncp = require('ncp').ncp;
		ncp.limit = 16;
		if (!fs.existsSync(destPath)){
			mkdir(destPath);
		}
		console.log('Copying server APKs from tmp build directory...');
		ncp(srcPath, destPath, function (err) {
			if (err) {
				return console.error(err);
			}
		});
	}
});

boilerplate({
  build: 'appium-uiautomator2-installer',
  jscs: false,
  extraPrepublishTasks: ['build'],
  e2eTest: {android: true},
  testTimeout: 20000
});
