
var gulp = require('gulp'),
	uglify = require('gulp-uglify'),
	sass = require('gulp-sass');

sass.compiler = require('node-sass');

gulp.task('sass', function() {
	return gulp.src('./sass/**/*.scss')
		.pipe(sass({outputStyle: 'compressed'}).on('error', sass.logError))
		.pipe(gulp.dest('./src/main/resources/static/build'));
});

gulp.task('sass:watch', function() {
	glup.watch('./sass/**/*.scss', ['sass']);
});

gulp.task('js', function() {
	return gulp.src('./js/*.js')
		.pipe(uglify())
		.pipe(gulp.dest('./src/main/resources/static/build'));
});

