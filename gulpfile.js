"use strict";
var teen_process = require('teen_process');
var runSequence = require('run-sequence');
var shell = require('gulp-shell');
//process.chdir('uiautomator2');
var gulp = require('gulp'),
    boilerplate = require('appium-gulp-plugins').boilerplate.use(gulp),
	fs = require('fs');

gulp.task('gradle-clean', shell.task([
        'gradle clean'
    ])
);

gulp.task('gradle-android', shell.task([
        'gradle assembleServerDebug'
    ])
);

gulp.task('gradle-androidTest', shell.task([
        'gradle assembleServerDebugandroidTest'
    ])
);

function mkdir(path, root) {
    var dirs = path.split('/'), dir = dirs.shift(), root = (root || '') + dir + '/';	
    try { fs.mkdirSync(root); }
    catch (e) {
        //dir wasn't made, something went wrong
        if(!fs.statSync(root).isDirectory()) throw new Error(e);
    }

    return !dirs.length || mkdir(dirs.join('/'), root);
};

//copy apk from tmp location current repo, apllicable only for Windows OS
gulp.task('copy_apks',function () {
	if(process.platform==="win32"){
		var destPath='./appium-uiautomator2-server/build/outputs/';
		var srcPath = 'C:\\tmp\\uiautomator2\\appium-uiautomator2-server\\outputs\\apk\\';
		var ncp = require('ncp').ncp;
		ncp.limit = 16;
		if (!fs.existsSync(destPath)){
			mkdir(destPath);
		}
		console.log('Copying apks from tmp...');
		ncp(srcPath, destPath, function (err) {
			if (err) {
				return console.error(err);
			}
		});
	}
});

gulp.task('build', function(callback) {
  runSequence('gradle-clean',
              'gradle-android',
              'gradle-androidTest',
			  'copy_apks',
              callback);
});

boilerplate({
  build: 'appium-uiautomator2-installer',
  jscs: false,
  extraPrepublishTasks: ['build'],
  e2eTest: {android: true},
  testTimeout: 20000
});

