declare i32 @printf(i8*, ...)
declare i8* @malloc(i64)
declare i8 @getchar()
declare i32 @__isoc99_scanf(i8*, ...)
declare void @exit(i32)
declare float @llvm.powi.f32(float  %Val, i32 %power)

; The IO class has no variable only functions
%class.IO = type {}

define %class.IO* @.New.IO() {
%1 = getelementptr %class.IO, %class.IO* null, i32 1
%2 = ptrtoint %class.IO* %1 to i64
%3 = call i8* @malloc(i64 %2)
%4 = ptrtoint i8* %3 to i64
%5 = inttoptr i64 %4 to %class.IO*
ret %class.IO* %5
}


define %class.IO* @IO.print(%class.IO* %self, i8* %toprint) {
  %1 = icmp eq %class.IO* null, %self
  br i1 %1, label %cond.true, label %cond.false
  cond.true:
    %2 = alloca [79 x i8]
    store [79 x i8] c"Segmentation fault : dispatch on null when calling function print on class IO\0a\00", [79 x i8]* %2
    %3 = getelementptr inbounds [79 x i8], [79 x i8]* %2, i32 0, i32 0
    %4 = call i32 (i8*, ...) @printf(i8* %3)
    call void @exit(i32 -1)
    ret %class.IO* null
  cond.false:
    call i32 (i8*, ...) @printf(i8* %toprint)
    ret %class.IO* %self
}

define %class.IO* @IO.printBool(%class.IO* %self, i1 %toprint) {
  %1 = icmp eq %class.IO* null, %self
  br i1 %1, label %cond.true, label %cond.false
  cond.true:
    %2 = alloca [83 x i8]
    store [83 x i8] c"Segmentation fault : dispatch on null when calling function printBool on class IO\0a\00", [83 x i8]* %2
    %3 = getelementptr inbounds [83 x i8], [83 x i8]* %2, i32 0, i32 0
    %4 = call i32 (i8*, ...) @printf(i8* %3)
    call void @exit(i32 -1)
    ret %class.IO* null
  cond.false:
    br i1 %toprint, label %print_true, label %print_false
    print_true:
      call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @.str.true.print, i32 0, i32 0))
      br label %end_print
    print_false:
      call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([6 x i8], [6 x i8]* @.str.false.print, i32 0, i32 0))
      br label %end_print
    end_print:
      ret %class.IO* %self
}

define %class.IO* @IO.printInt32(%class.IO* %self, i32 %toprint) {
  %1 = icmp eq %class.IO* null, %self
  br i1 %1, label %cond.true, label %cond.false
  cond.true:
    %2 = alloca [84 x i8]
    store [84 x i8] c"Segmentation fault : dispatch on null when calling function printInt32 on class IO\0a\00", [84 x i8]* %2
    %3 = getelementptr inbounds [84 x i8], [84 x i8]* %2, i32 0, i32 0
    %4 = call i32 (i8*, ...) @printf(i8* %3)
    call void @exit(i32 -1)
    ret %class.IO* null
  cond.false:
    call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str.formatInt32, i32 0, i32 0), i32 %toprint)
    ret %class.IO* %self
}

define i8* @IO.inputLine(%class.IO* %self) {
  %1 = icmp eq %class.IO* null, %self
  br i1 %1, label %cond.true, label %cond.false
  cond.true:
    %2 = alloca [83 x i8]
    store [83 x i8] c"Segmentation fault : dispatch on null when calling function inputLine on class IO\0a\00", [83 x i8]* %2
    %3 = getelementptr inbounds [83 x i8], [83 x i8]* %2, i32 0, i32 0
    %4 = call i32 (i8*, ...) @printf(i8* %3)
    call void @exit(i32 -1)
    ret i8* null
  cond.false:
    %5 = call i8* @malloc(i64 1000)
    %6 = icmp eq i8* %5, null
    br i1 %6, label %bad_malloc, label %good_malloc
    good_malloc:
      %i = alloca i32
      store i32 0, i32* %i
      br label %input_line_loop_start
      input_line_loop_start:
        %7 = call i8 @getchar()
        %8 = load i32, i32* %i
        %9 = getelementptr inbounds i8, i8* %5, i32 %8
        store i8 %7, i8* %9
        %10 = add i32 %8, 1
        store i32 %10, i32* %i
        ; 10 is newline
        %11 = icmp eq i8 %7, 10
        br i1 %11, label %input_line_loop_end, label %input_line_loop_start

      input_line_loop_end:
        ret i8* %5

    bad_malloc:
      ret i8* getelementptr inbounds ([1 x i8], [1 x i8]* @.str.emptyStr, i32 0, i32 0)
}

define i1 @IO.inputBool(%class.IO* %self) {
  %1 = icmp eq %class.IO* null, %self
  br i1 %1, label %cond.true, label %cond.false
  cond.true:
    %2 = alloca [83 x i8]
    store [83 x i8] c"Segmentation fault : dispatch on null when calling function inputBool on class IO\0a\00", [83 x i8]* %2
    %3 = getelementptr inbounds [83 x i8], [83 x i8]* %2, i32 0, i32 0
    %4 = call i32 (i8*, ...) @printf(i8* %3)
    call void @exit(i32 -1)
    ret i1 0
  cond.false:
    %5 = call i8* @IO.inputLine(%class.IO* %self)
    ; This will return the first character
    %6 = load i8, i8* %5
    %7 = load i8, i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.y, i32 0, i32 0)
    %8 = icmp eq i8 %7, %6
    br i1 %8, label %input_yes, label %input_not_yes
    input_yes:
      ret i1 1
    input_not_yes:
      %9 = load i8, i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.n, i32 0, i32 0)
      %10 = icmp eq i8 %9, %6
      br i1 %10, label %input_no, label %second_chance
      input_no:
        ret i1 0

    second_chance:
      %11 = load i8, i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.one, i32 0, i32 0)
      %12 = icmp eq i8 %11, %6
      br i1 %12, label %input_yes, label %input_not_one

      input_not_one:
        %13 = load i8, i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.zero, i32 0, i32 0)
        %14 = icmp eq i8 %13, %6
        br i1 %14, label %input_no, label %third_chance

    third_chance:
      %i = alloca i32
      store i32 0, i32* %i
      br label %input_bool_true_loop_start
      input_bool_true_loop_start:
        %15 = load i32, i32* %i
        %16 = getelementptr inbounds [6 x i8], [6 x i8]* @.str.true.input, i32 0, i32 %15
        %17 = getelementptr inbounds i8, i8* %5, i32 %15
        %18 = load i8, i8* %16
        %19 = load i8, i8* %17
        %20 = icmp eq i8 %18, %19
        br i1 %20, label %true_loop_cont, label %input_not_true
        true_loop_cont:
          %21 = add i32 %15, 1
          store i32 %21, i32* %i
          %22 = icmp ugt i32 %21, 5
          br i1 %22, label %input_yes, label %input_bool_true_loop_start

      input_not_true:
        store i32 0, i32* %i
        br label %false_loop_start
        false_loop_start:
          %23 = load i32, i32* %i
          %24 = getelementptr inbounds [7 x i8], [7 x i8]* @.str.false.input, i32 0, i32 %23
          %25 = getelementptr inbounds i8, i8* %5, i32 %23
          %26 = load i8, i8* %24
          %27 = load i8, i8* %25
          %28 = icmp eq i8 %26, %27
          br i1 %28, label %false_loop_cont, label %bad_input
          false_loop_cont:
            %29 = add i32 %23, 1
            store i32 %29, i32* %i
            %30 = icmp ugt i32 %29, 6
            br i1 %30, label %input_no, label %false_loop_start

    bad_input:      
      call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([104 x i8], [104 x i8]* @.str.bad_bool, i32 0, i32 0))
      call void @exit(i32 -1)
      ret i1 0
}

; TODO : return a random number if input is not a number, -1 if too large
define i32 @IO.inputInt32(%class.IO* %self) {
  %1 = icmp eq %class.IO* null, %self
  br i1 %1, label %cond.true, label %cond.false
  cond.true:
    %2 = alloca [84 x i8]
    store [84 x i8] c"Segmentation fault : dispatch on null when calling function inputInt32 on class IO\0a\00", [84 x i8]* %2
    %3 = getelementptr inbounds [84 x i8], [84 x i8]* %2, i32 0, i32 0
    %4 = call i32 (i8*, ...) @printf(i8* %3)
    call void @exit(i32 -1)
    ret i32 0
  cond.false:
    %5 = alloca i32
    call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str.formatInt32, i32 0, i32 0), i32* %5)
    ; 7 because call above return %6
    %7 = load i32, i32* %5

    ; read until newline char
    br label %input_int32_loop_start
    input_int32_loop_start:
      %8 = call i8 @getchar()
      ; 10 is newline
      %9 = icmp eq i8 %8, 10
      br i1 %9, label %input_int32_loop_end, label %input_int32_loop_start

    input_int32_loop_end:
      br label %input_int32_end

    input_int32_end:
      ret i32 %7
}

@.str.formatInt32 = private unnamed_addr constant [3 x i8] c"%d\00"

@.str.true.print = private unnamed_addr constant [5 x i8] c"true\00"
@.str.false.print = private unnamed_addr constant [6 x i8] c"false\00"

@.str.y = private unnamed_addr constant [2 x i8] c"y\00"
@.str.n = private unnamed_addr constant [2 x i8] c"n\00"
@.str.true.input = private unnamed_addr constant [6 x i8] c"true\0a\00"
@.str.false.input = private unnamed_addr constant [7 x i8] c"false\0a\00"
@.str.zero = private unnamed_addr constant [2 x i8] c"0\00"
@.str.one = private unnamed_addr constant [2 x i8] c"1\00"
@.str.inputLine = private unnamed_addr constant [6 x i8] c"%999s\00"

@.str.emptyStr = private unnamed_addr constant [1 x i8] c"\00"
@.str.bad_bool = private unnamed_addr constant [104 x i8] c"Boolean input should be either\0a'y' or 'true' or '1' for saying yes\0a'n' or 'false' or '0' for saying no\0a\00"

%class.Object = type {  }

define %class.Object* @.New.Object() {
%1 = getelementptr %class.Object, %class.Object* null, i32 1
%2 = ptrtoint %class.Object* %1 to i64
%3 = call i8* @malloc(i64 %2)
%4 = ptrtoint i8* %3 to i64
%5 = inttoptr i64 %4 to %class.Object*
ret %class.Object* %5
}
