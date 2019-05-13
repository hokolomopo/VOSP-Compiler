declare i32 @printf(i8*, ...)
declare i8* @malloc(i64)
declare i32 @__isoc99_scanf(i8*, ...)
declare void @exit(i32)
declare float @llvm.powi.f32(float  %Val, i32 %power)

; The IO class has no variable only functions
%class.IO = type {}

define %class.IO* @IO.print(%class.IO* %self, i8*) {
  call i32 (i8*, ...) @printf(i8* %0)
  ret %class.IO* %self
}

define %class.IO* @IO.printBool(%class.IO* %self, i1) {
  br i1 %0, label %print_true, label %print_false
  print_true:
    call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([6 x i8], [6 x i8]* @.str.true, i32 0, i32 0))
    br label %end_print
  print_false:
    call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([7 x i8], [7 x i8]* @.str.false, i32 0, i32 0))
    br label %end_print
  end_print:
    ret %class.IO* %self
}

define %class.IO* @IO.printInt32(%class.IO* %self, i32) {
  call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str.formatInt32, i32 0, i32 0), i32 %0)
  ret %class.IO* %self
}

define i8* @IO.inputLine(%class.IO* %self) {
  %1 = call i8* @malloc(i64 1000)
  %2 = icmp eq i8* %1, null
  br i1 %2, label %bad_malloc, label %good_malloc
  good_malloc:
    call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str.inputLine, i32 0, i32 0), i8* %1)
    ret i8* %1
  bad_malloc:
    ret i8* getelementptr inbounds ([1 x i8], [1 x i8]* @.str.emptyStr, i32 0, i32 0)
}

define i1 @IO.inputBool(%class.IO* %self) {
  %1 = call i8* @IO.inputLine(%class.IO* %self)
  %2 = load i8, i8* %1
  %3 = load i8, i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.y, i32 0, i32 0)
  %4 = icmp eq i8 %3, %2
  br i1 %4, label %input_yes, label %input_not_yes
  input_yes:
    ret i1 1
  input_not_yes:
    %5 = load i8, i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.n, i32 0, i32 0)
    %6 = icmp eq i8 %5, %2
    br i1 %6, label %input_no, label %bad_input
    input_no:
      ret i1 0
    bad_input:
      call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([43 x i8], [43 x i8]* @.str.bad_bool, i32 0, i32 0))
      call void @exit(i32 -1)
      ret i1 0
}

; TODO : return a random number if input is not a number, -1 if too large
define i32 @IO.inputInt32(%class.IO* %self) {
  %1 = alloca i32
  call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str.formatInt32, i32 0, i32 0), i32* %1)
  %3 = load i32, i32* %1
  ret i32 %3
}

@.str.formatInt32 = private unnamed_addr constant [3 x i8] c"%d\00"
@.str.true = private unnamed_addr constant [6 x i8] c"true\0a\00"
@.str.false = private unnamed_addr constant [7 x i8] c"false\0a\00"
@.str.inputLine = private unnamed_addr constant [3 x i8] c"%s\00"
@.str.emptyStr = private unnamed_addr constant [1 x i8] c"\00"
@.str.y = private unnamed_addr constant [2 x i8] c"y\00"
@.str.n = private unnamed_addr constant [2 x i8] c"n\00"
@.str.bad_bool = private unnamed_addr constant [43 x i8] c"Boolean input should be either 'y' or 'n'\0a\00"
@.str.bad_int32 = private unnamed_addr constant [50 x i8] c"Error while loading int32 value, maybe too long?\0a\00"
