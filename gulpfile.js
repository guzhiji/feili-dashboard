
var buildPath = './src/main/resources/static/build',
	gulp = require('gulp'),
	uglify = require('gulp-uglify'),
	sass = require('gulp-sass')(require('sass')),
	concat = require('gulp-concat'),
	rm = require('gulp-rm');

gulp.task('sass', function() {
	return gulp.src('./sass/**/*.scss')
		.pipe(sass({outputStyle: 'compressed'}).on('error', sass.logError))
		.pipe(concat('styles.min.css', {newLine: ''}))
		.pipe(gulp.dest(buildPath));
});

gulp.task('sass:watch', function() {
	glup.watch('./sass/**/*.scss', ['sass']);
});

gulp.task('js', function() {
	return gulp.src('./js/*.js')
		.pipe(uglify())
		.pipe(gulp.dest(buildPath));
});

gulp.task('clean', function() {
	return gulp.src(buildPath + '/*', {read: false})
		.pipe(rm());
});
