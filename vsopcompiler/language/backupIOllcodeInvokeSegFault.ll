define %class.IO* @IO.print(%class.IO* %self, i8*) {
  call i32 (i8*, ...) @printf(i8* %0)
  ret %class.IO* %self
}

define %class.IO* @IO.printBool(%class.IO* %self, i1) {
  br i1 %0, label %print_true, label %print_false
  print_true:
    call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @.str.true, i32 0, i32 0))
    ret %class.IO* %self
  print_false:
    call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([6 x i8], [6 x i8]* @.str.false, i32 0, i32 0))
    ret %class.IO* %self
}

define %class.IO* @IO.formatInt32(%class.IO* %self, i32) {
  call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str.formatInt32, i32 0, i32 0), i32 %0)
  ret %class.IO* %self
}

declare i32 @printf(i8*, ...)

define i8* @IO.inputLine() {
  %1 = invoke i8* @malloc(i64 1000) to label %good_malloc unwind label %fail
  good_malloc:
    invoke i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str.inputLine, i32 0, i32 0), i8* %1) to label %good_scanf unwind label %fail
    good_scanf:
      ret i8* %1
  fail:
    ret i8* getelementptr inbounds ([1 x i8], [1 x i8]* @.str.emptyStr, i32 0, i32 0)
}

define i1 @IO.inputBool() {
  %1 = call i8* @IO.inputLine()
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
      call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([42 x i8], [42 x i8]* @.str.bad_bool, i32 0, i32 0))
      call void @exit(i32 -1)
      ret i1 0
}

define i32 @IO.inputInt32() {
  %1 = alloca i32
  invoke i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str.formatInt32, i32 0, i32 0), i32* %1) to label %good_scanf unwind label %fail
    good_scanf:
      %3 = load i32, i32* %1
      ret i32 %3
  fail:
    call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([49 x i8], [49 x i8]* @.str.bad_int32, i32 0, i32 0))
    call void @exit(i32 -1)
    ret i32 0
}

declare i8* @malloc(i64)

declare i32 @__isoc99_scanf(i8*, ...)

@.str.formatInt32 = private unnamed_addr constant [3 x i8] c"%d\00"
@.str.true = private unnamed_addr constant [5 x i8] c"true\00"
@.str.false = private unnamed_addr constant [6 x i8] c"false\00"
@.str.inputLine = private unnamed_addr constant [3 x i8] c"%s\00"
@.str.emptyStr = private unnamed_addr constant [1 x i8] c"\00"
@.str.y = private unnamed_addr constant [2 x i8] c"y\00"
@.str.n = private unnamed_addr constant [2 x i8] c"n\00"
@.str.bad_bool = private unnamed_addr constant [42 x i8] c"Boolean input should be either 'y' or 'n'\00"
@.str.bad_int32 = private unnamed_addr constant [49 x i8] c"Error while loading int32 value, maybe too long?\00"
