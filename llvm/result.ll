%Vtable.A = type { }

%class.A = type { %Vtable.A* }

%Vtable.IO = type { %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.IO*, i1)*, %class.IO* (%class.IO*, i32)*, i8* (%class.IO*)*, i1 (%class.IO*)*, i32 (%class.IO*)*}

%class.IO = type { %Vtable.IO* }

%Vtable.X = type { %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.IO*, i1)*, %class.IO* (%class.IO*, i32)*, i8* (%class.IO*)*, i1 (%class.IO*)*, i32 (%class.IO*)*, i32 (%class.X*)*}

%class.X = type { %Vtable.X*, i32, i1 }

%Vtable.Y = type { %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.IO*, i1)*, %class.IO* (%class.IO*, i32)*, i8* (%class.IO*)*, i1 (%class.IO*)*, i32 (%class.IO*)*, i32 (%class.Y*)*, %class.Y* (%class.Y*)*, i32 (%class.Y*)*}

%class.Y = type { %Vtable.Y*, i32, i1, i32 }

%Vtable.Object = type { }

%class.Object = type { %Vtable.Object* }

%Vtable.Main = type { %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.IO*, i1)*, %class.IO* (%class.IO*, i32)*, i8* (%class.IO*)*, i1 (%class.IO*)*, i32 (%class.IO*)*, i32 (%class.Main*)*, %class.Main* (%class.Main*, i8*)*, i32 (%class.Main*, i32)*, i32 (%class.Main*)*, i32 (%class.Main*, i32)*, %class.Main* (%class.Main*)*, i32 (%class.Main*, i32, %class.Main*)*}

%class.Main = type { %Vtable.Main*, i1, i32, i8*, %class.X* }

declare i32 @printf(i8*, ...)
declare i8* @malloc(i64)
declare i8 @getchar()
declare i32 @__isoc99_scanf(i8*, ...)
declare void @exit(i32)
declare float @llvm.powi.f32(float  %Val, i32 %power)

; The IO class has no variable only functions
;%Vtable.IO = type { %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.IO*, i1)*, %class.IO* (%class.IO*, i32)*, i8* (%class.IO*)*, i1 (%class.IO*)*, i32 (%class.IO*)*}
;%class.IO = type { %Vtable.IO* }

;%class.Object = type { %Vtable.Object* }
;%Vtable.Object = type { }

define %class.IO* @.New.IO() {
%1 = getelementptr %class.IO, %class.IO* null, i32 1
%2 = ptrtoint %class.IO* %1 to i64
%3 = call i8* @malloc(i64 %2)
%4 = ptrtoint i8* %3 to i64
%5 = inttoptr i64 %4 to %class.IO*
%6 = getelementptr %Vtable.IO, %Vtable.IO* null, i32 1
%7 = ptrtoint %Vtable.IO* %6 to i64
%8 = call i8* @malloc(i64 %7)
%9 = ptrtoint i8* %8 to i64
%10 = inttoptr i64 %9 to %Vtable.IO*
%11 = getelementptr %class.IO, %class.IO* %5, i32 0, i32 0 
store %Vtable.IO* %10, %Vtable.IO** %11 
call void @.Init.IO (%class.IO* %5)
ret %class.IO* %5
}

define void @.Init.IO(%class.IO* %self) {
%1 = ptrtoint %class.IO* %self to i64
%2 = inttoptr i64 %1 to %class.Object*
call void @.Init.Object (%class.Object* %2)
%3 = getelementptr %class.IO, %class.IO* %self, i32 0, i32 0 
%4 = load %Vtable.IO*, %Vtable.IO** %3 
%5 = getelementptr %Vtable.IO, %Vtable.IO* %4, i32 0, i32 0 
store %class.IO* (%class.IO*, i8*)* @IO.print, %class.IO* (%class.IO*, i8*)** %5 
%6 = getelementptr %Vtable.IO, %Vtable.IO* %4, i32 0, i32 1 
store %class.IO* (%class.IO*, i1)* @IO.printBool, %class.IO* (%class.IO*, i1)** %6 
%7 = getelementptr %Vtable.IO, %Vtable.IO* %4, i32 0, i32 2 
store %class.IO* (%class.IO*, i32)* @IO.printInt32, %class.IO* (%class.IO*, i32)** %7 
%8 = getelementptr %Vtable.IO, %Vtable.IO* %4, i32 0, i32 3 
store i8* (%class.IO*)* @IO.inputLine, i8* (%class.IO*)** %8 
%9 = getelementptr %Vtable.IO, %Vtable.IO* %4, i32 0, i32 4 
store i1 (%class.IO*)* @IO.inputBool, i1 (%class.IO*)** %9 
%10 = getelementptr %Vtable.IO, %Vtable.IO* %4, i32 0, i32 5 
store i32 (%class.IO*)* @IO.inputInt32, i32 (%class.IO*)** %10 
ret void
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


define %class.Object* @.New.Object() {
%1 = getelementptr %class.Object, %class.Object* null, i32 1
%2 = ptrtoint %class.Object* %1 to i64
%3 = call i8* @malloc(i64 %2)
%4 = ptrtoint i8* %3 to i64
%5 = inttoptr i64 %4 to %class.Object*
%6 = getelementptr %Vtable.Object, %Vtable.Object* null, i32 1
%7 = ptrtoint %Vtable.Object* %6 to i64
%8 = call i8* @malloc(i64 %7)
%9 = ptrtoint i8* %8 to i64
%10 = inttoptr i64 %9 to %Vtable.Object*
%11 = getelementptr %class.Object, %class.Object* %5, i32 0, i32 0 
store %Vtable.Object* %10, %Vtable.Object** %11 
call void @.Init.Object (%class.Object* %5)
ret %class.Object* %5
}

define void @.Init.Object(%class.Object* %self) {
ret void
}

@.str = private unnamed_addr constant [39 x i8] c"Viva la vida baby and keep on Rockin'\0a\00"
@.str.1 = private unnamed_addr constant [39 x i8] c"Viva la vida baby and keep on Rockin'\0a\00"
@.str.2 = private unnamed_addr constant [4 x i8] c"lol\00"
@.str.3 = private unnamed_addr constant [2 x i8] c"\0a\00"
@.str.4 = private unnamed_addr constant [12 x i8] c"X is null \0a\00"
@.str.5 = private unnamed_addr constant [2 x i8] c"\0a\00"


define i32 @main () { 
%1 = call %class.Main* @.New.Main()
%returned = call i32 @Main.main(%class.Main* %1)
ret i32 %returned
}

define %class.Main* @.New.Main() {
%1 = getelementptr %class.Main, %class.Main* null, i32 1
%2 = ptrtoint %class.Main* %1 to i64
%3 = call i8* @malloc(i64 %2)
%4 = ptrtoint i8* %3 to i64
%5 = inttoptr i64 %4 to %class.Main*
%6 = getelementptr %Vtable.Main, %Vtable.Main* null, i32 1
%7 = ptrtoint %Vtable.Main* %6 to i64
%8 = call i8* @malloc(i64 %7)
%9 = ptrtoint i8* %8 to i64
%10 = inttoptr i64 %9 to %Vtable.Main*
%11 = getelementptr %class.Main, %class.Main* %5, i32 0, i32 0 
store %Vtable.Main* %10, %Vtable.Main** %11 
call void @.Init.Main (%class.Main* %5)
ret %class.Main* %5
}

define void @.Init.Main(%class.Main* %self) {
%1 = ptrtoint %class.Main* %self to i64
%2 = inttoptr i64 %1 to %class.IO*
call void @.Init.IO (%class.IO* %2)
%3 = getelementptr %class.Main, %class.Main* %self, i32 0, i32 1 
store i1 1, i1* %3 
%4 = getelementptr %class.Main, %class.Main* %self, i32 0, i32 2 
store i32 0, i32* %4 
%5 = getelementptr %class.Main, %class.Main* %self, i32 0, i32 3 
store i8* getelementptr inbounds ([39 x i8], [39 x i8]* @.str, i32 0, i32 0), i8** %5 
%6 = call %class.X* @.New.X()
%7 = getelementptr %class.Main, %class.Main* %self, i32 0, i32 4 
store %class.X* %6, %class.X** %7 
%8 = getelementptr %class.Main, %class.Main* %self, i32 0, i32 0 
%9 = load %Vtable.Main*, %Vtable.Main** %8 
%10 = getelementptr %Vtable.Main, %Vtable.Main* %9, i32 0, i32 6 
store i32 (%class.Main*)* @Main.main, i32 (%class.Main*)** %10 
%11 = getelementptr %Vtable.Main, %Vtable.Main* %9, i32 0, i32 7 
store %class.Main* (%class.Main*, i8*)* @Main.testString, %class.Main* (%class.Main*, i8*)** %11 
%12 = getelementptr %Vtable.Main, %Vtable.Main* %9, i32 0, i32 8 
store i32 (%class.Main*, i32)* @Main.testLoop, i32 (%class.Main*, i32)** %12 
%13 = getelementptr %Vtable.Main, %Vtable.Main* %9, i32 0, i32 9 
store i32 (%class.Main*)* @Main.testNew, i32 (%class.Main*)** %13 
%14 = getelementptr %Vtable.Main, %Vtable.Main* %9, i32 0, i32 10 
store i32 (%class.Main*, i32)* @Main.testLoopCond, i32 (%class.Main*, i32)** %14 
%15 = getelementptr %Vtable.Main, %Vtable.Main* %9, i32 0, i32 11 
store %class.Main* (%class.Main*)* @Main.getSelf, %class.Main* (%class.Main*)** %15 
%16 = getelementptr %Vtable.Main, %Vtable.Main* %9, i32 0, i32 12 
store i32 (%class.Main*, i32, %class.Main*)* @Main.truc, i32 (%class.Main*, i32, %class.Main*)** %16 
ret void
}

define i32 @Main.main(%class.Main* %self) {
%1 = icmp eq %class.Main* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [80 x i8]
store [80 x i8] c"Segmentation fault : dispatch on null when calling function main on class Main\0a\00", [80 x i8]* %2
%3 = getelementptr inbounds [80 x i8], [80 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret i32 0
cond.false:
%self.ptr = alloca %class.Main* 
store %class.Main* %self, %class.Main** %self.ptr 
%5 = load %class.Main*, %class.Main** %self.ptr 
%6 = ptrtoint %class.Main* %5 to i64
%7 = inttoptr i64 %6 to %class.IO*
%8 = getelementptr %class.Main, %class.Main* %self, i32 0, i32 4 
%9 = load %class.X*, %class.X** %8 
%10 = icmp eq %class.X* null, %9
%11 = getelementptr %class.IO, %class.IO* %7, i32 0, i32 0 
%12 = load %Vtable.IO*, %Vtable.IO** %11 
%13 = getelementptr %Vtable.IO, %Vtable.IO* %12, i32 0, i32 1 
%14 = load %class.IO* (%class.IO*, i1)*, %class.IO* (%class.IO*, i1)** %13 
%15 = call %class.IO* %14(%class.IO* %7, i1 %10)
%16 = load %class.Main*, %class.Main** %self.ptr 
%17 = getelementptr %class.Main, %class.Main* %self, i32 0, i32 3 
%18 = load i8*, i8** %17 
%19 = getelementptr %class.Main, %class.Main* %16, i32 0, i32 0 
%20 = load %Vtable.Main*, %Vtable.Main** %19 
%21 = getelementptr %Vtable.Main, %Vtable.Main* %20, i32 0, i32 7 
%22 = load %class.Main* (%class.Main*, i8*)*, %class.Main* (%class.Main*, i8*)** %21 
%23 = call %class.Main* %22(%class.Main* %16, i8* %18)
%24 = load %class.Main*, %class.Main** %self.ptr 
%25 = getelementptr %class.Main, %class.Main* %24, i32 0, i32 0 
%26 = load %Vtable.Main*, %Vtable.Main** %25 
%27 = getelementptr %Vtable.Main, %Vtable.Main* %26, i32 0, i32 10 
%28 = load i32 (%class.Main*, i32)*, i32 (%class.Main*, i32)** %27 
%29 = call i32 %28(%class.Main* %24, i32 0)
%30 = load %class.Main*, %class.Main** %self.ptr 
%31 = getelementptr %class.Main, %class.Main* %30, i32 0, i32 0 
%32 = load %Vtable.Main*, %Vtable.Main** %31 
%33 = getelementptr %Vtable.Main, %Vtable.Main* %32, i32 0, i32 9 
%34 = load i32 (%class.Main*)*, i32 (%class.Main*)** %33 
%35 = call i32 %34(%class.Main* %30)
%36 = load %class.Main*, %class.Main** %self.ptr 
%37 = getelementptr %class.Main, %class.Main* %36, i32 0, i32 0 
%38 = load %Vtable.Main*, %Vtable.Main** %37 
%39 = getelementptr %Vtable.Main, %Vtable.Main* %38, i32 0, i32 11 
%40 = load %class.Main* (%class.Main*)*, %class.Main* (%class.Main*)** %39 
%41 = call %class.Main* %40(%class.Main* %36)
ret i32 0 
}

define %class.Main* @Main.testString(%class.Main* %self, i8* %myString) {
%1 = icmp eq %class.Main* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [86 x i8]
store [86 x i8] c"Segmentation fault : dispatch on null when calling function testString on class Main\0a\00", [86 x i8]* %2
%3 = getelementptr inbounds [86 x i8], [86 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret %class.Main* null
cond.false:
%self.ptr = alloca %class.Main* 
%myString.ptr = alloca i8* 
store %class.Main* %self, %class.Main** %self.ptr 
store i8* %myString, i8** %myString.ptr 
store i8* getelementptr inbounds ([39 x i8], [39 x i8]* @.str.1, i32 0, i32 0), i8** %myString.ptr 
%5 = load %class.Main*, %class.Main** %self.ptr 
%6 = ptrtoint %class.Main* %5 to i64
%7 = inttoptr i64 %6 to %class.IO*
%8 = load i8*, i8** %myString.ptr 
%9 = getelementptr %class.IO, %class.IO* %7, i32 0, i32 0 
%10 = load %Vtable.IO*, %Vtable.IO** %9 
%11 = getelementptr %Vtable.IO, %Vtable.IO* %10, i32 0, i32 0 
%12 = load %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.IO*, i8*)** %11 
%13 = call %class.IO* %12(%class.IO* %7, i8* %8)
%14 = load %class.Main*, %class.Main** %self.ptr 
%15 = ptrtoint %class.Main* %14 to i64
%16 = inttoptr i64 %15 to %class.IO*
%17 = getelementptr %class.Main, %class.Main* %self, i32 0, i32 3 
%18 = load i8*, i8** %17 
%19 = getelementptr %class.IO, %class.IO* %16, i32 0, i32 0 
%20 = load %Vtable.IO*, %Vtable.IO** %19 
%21 = getelementptr %Vtable.IO, %Vtable.IO* %20, i32 0, i32 0 
%22 = load %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.IO*, i8*)** %21 
%23 = call %class.IO* %22(%class.IO* %16, i8* %18)
%24 = load %class.Main*, %class.Main** %self.ptr 
%25 = ptrtoint %class.Main* %24 to i64
%26 = inttoptr i64 %25 to %class.IO*
%27 = load i8*, i8** %myString.ptr 
%28 = getelementptr %class.IO, %class.IO* %26, i32 0, i32 0 
%29 = load %Vtable.IO*, %Vtable.IO** %28 
%30 = getelementptr %Vtable.IO, %Vtable.IO* %29, i32 0, i32 0 
%31 = load %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.IO*, i8*)** %30 
%32 = call %class.IO* %31(%class.IO* %26, i8* %27)
%33 = load %class.Main*, %class.Main** %self.ptr 
ret %class.Main* %33 
}

define i32 @Main.testLoop(%class.Main* %self, i32 %i) {
%1 = icmp eq %class.Main* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [84 x i8]
store [84 x i8] c"Segmentation fault : dispatch on null when calling function testLoop on class Main\0a\00", [84 x i8]* %2
%3 = getelementptr inbounds [84 x i8], [84 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret i32 0
cond.false:
%self.ptr = alloca %class.Main* 
%i.ptr = alloca i32 
store %class.Main* %self, %class.Main** %self.ptr 
store i32 %i, i32* %i.ptr 
br label %loop.cond
loop.cond:
%5 = load i32, i32* %i.ptr 
%6 = icmp slt i32 %5, 3
br i1 %6, label %loop.start, label %loop.end

loop.start:
%7 = load %class.Main*, %class.Main** %self.ptr 
%8 = ptrtoint %class.Main* %7 to i64
%9 = inttoptr i64 %8 to %class.IO*
%10 = load i32, i32* %i.ptr 
%11 = getelementptr %class.IO, %class.IO* %9, i32 0, i32 0 
%12 = load %Vtable.IO*, %Vtable.IO** %11 
%13 = getelementptr %Vtable.IO, %Vtable.IO* %12, i32 0, i32 2 
%14 = load %class.IO* (%class.IO*, i32)*, %class.IO* (%class.IO*, i32)** %13 
%15 = call %class.IO* %14(%class.IO* %9, i32 %10)
%16 = load i32, i32* %i.ptr 
%17 = add i32 %16, 1
store i32 %17, i32* %i.ptr 
br label %loop.cond

loop.end:
%18 = load %class.Main*, %class.Main** %self.ptr 
%19 = ptrtoint %class.Main* %18 to i64
%20 = inttoptr i64 %19 to %class.IO*
%21 = getelementptr %class.IO, %class.IO* %20, i32 0, i32 0 
%22 = load %Vtable.IO*, %Vtable.IO** %21 
%23 = getelementptr %Vtable.IO, %Vtable.IO* %22, i32 0, i32 0 
%24 = load %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.IO*, i8*)** %23 
%25 = call %class.IO* %24(%class.IO* %20, i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.3, i32 0, i32 0))
%26 = load i32, i32* %i.ptr 
ret i32 %26 
}

define i32 @Main.testNew(%class.Main* %self) {
%1 = icmp eq %class.Main* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [83 x i8]
store [83 x i8] c"Segmentation fault : dispatch on null when calling function testNew on class Main\0a\00", [83 x i8]* %2
%3 = getelementptr inbounds [83 x i8], [83 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret i32 0
cond.false:
%self.ptr = alloca %class.Main* 
store %class.Main* %self, %class.Main** %self.ptr 
%5 = alloca %class.Y* 
%6 = call %class.Y* @.New.Y()
store %class.Y* %6, %class.Y** %5 
%cond2.ptr = alloca %class.IO* 
%7 = getelementptr %class.Main, %class.Main* %self, i32 0, i32 4 
%8 = load %class.X*, %class.X** %7 
%9 = icmp eq %class.X* null, %8
br i1 %9, label %cond2.true, label %cond2.false

cond2.true:
%10 = load %class.Main*, %class.Main** %self.ptr 
%11 = ptrtoint %class.Main* %10 to i64
%12 = inttoptr i64 %11 to %class.IO*
%13 = getelementptr %class.IO, %class.IO* %12, i32 0, i32 0 
%14 = load %Vtable.IO*, %Vtable.IO** %13 
%15 = getelementptr %Vtable.IO, %Vtable.IO* %14, i32 0, i32 0 
%16 = load %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.IO*, i8*)** %15 
%17 = call %class.IO* %16(%class.IO* %12, i8* getelementptr inbounds ([12 x i8], [12 x i8]* @.str.4, i32 0, i32 0))
store %class.IO* %17, %class.IO** %cond2.ptr 
br label %cond2.end

cond2.false:
%18 = load %class.Main*, %class.Main** %self.ptr 
%19 = ptrtoint %class.Main* %18 to i64
%20 = inttoptr i64 %19 to %class.IO*
%21 = load %class.Y*, %class.Y** %5 
%22 = getelementptr %class.Y, %class.Y* %21, i32 0, i32 0 
%23 = load %Vtable.Y*, %Vtable.Y** %22 
%24 = getelementptr %Vtable.Y, %Vtable.Y* %23, i32 0, i32 7 
%25 = load %class.Y* (%class.Y*)*, %class.Y* (%class.Y*)** %24 
%26 = call %class.Y* %25(%class.Y* %21)
%27 = getelementptr %class.Y, %class.Y* %26, i32 0, i32 0 
%28 = load %Vtable.Y*, %Vtable.Y** %27 
%29 = getelementptr %Vtable.Y, %Vtable.Y* %28, i32 0, i32 6 
%30 = load i32 (%class.Y*)*, i32 (%class.Y*)** %29 
%31 = call i32 %30(%class.Y* %26)
%32 = getelementptr %class.IO, %class.IO* %20, i32 0, i32 0 
%33 = load %Vtable.IO*, %Vtable.IO** %32 
%34 = getelementptr %Vtable.IO, %Vtable.IO* %33, i32 0, i32 2 
%35 = load %class.IO* (%class.IO*, i32)*, %class.IO* (%class.IO*, i32)** %34 
%36 = call %class.IO* %35(%class.IO* %20, i32 %31)
store %class.IO* %36, %class.IO** %cond2.ptr 
br label %cond2.end

cond2.end:
%37 = load %class.IO*, %class.IO** %cond2.ptr 
ret i32 3 
}

define i32 @Main.testLoopCond(%class.Main* %self, i32 %i) {
%1 = icmp eq %class.Main* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [88 x i8]
store [88 x i8] c"Segmentation fault : dispatch on null when calling function testLoopCond on class Main\0a\00", [88 x i8]* %2
%3 = getelementptr inbounds [88 x i8], [88 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret i32 0
cond.false:
%self.ptr = alloca %class.Main* 
%i.ptr = alloca i32 
store %class.Main* %self, %class.Main** %self.ptr 
store i32 %i, i32* %i.ptr 
%5 = load i32, i32* %i.ptr 
%6 = icmp eq i32 %5, 0
br i1 %6, label %cond2.true, label %cond2.end

cond2.true:
br label %loop.cond
loop.cond:
%7 = load i32, i32* %i.ptr 
%8 = icmp slt i32 %7, 3
br i1 %8, label %loop.start, label %loop.end

loop.start:
%9 = load %class.Main*, %class.Main** %self.ptr 
%10 = ptrtoint %class.Main* %9 to i64
%11 = inttoptr i64 %10 to %class.IO*
%12 = load i32, i32* %i.ptr 
%13 = getelementptr %class.IO, %class.IO* %11, i32 0, i32 0 
%14 = load %Vtable.IO*, %Vtable.IO** %13 
%15 = getelementptr %Vtable.IO, %Vtable.IO* %14, i32 0, i32 2 
%16 = load %class.IO* (%class.IO*, i32)*, %class.IO* (%class.IO*, i32)** %15 
%17 = call %class.IO* %16(%class.IO* %11, i32 %12)
%18 = load i32, i32* %i.ptr 
%19 = add i32 %18, 1
store i32 %19, i32* %i.ptr 
br label %loop.cond

loop.end:
br label %cond2.end

cond2.end:
%20 = load %class.Main*, %class.Main** %self.ptr 
%21 = ptrtoint %class.Main* %20 to i64
%22 = inttoptr i64 %21 to %class.IO*
%23 = getelementptr %class.IO, %class.IO* %22, i32 0, i32 0 
%24 = load %Vtable.IO*, %Vtable.IO** %23 
%25 = getelementptr %Vtable.IO, %Vtable.IO* %24, i32 0, i32 0 
%26 = load %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.IO*, i8*)** %25 
%27 = call %class.IO* %26(%class.IO* %22, i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.5, i32 0, i32 0))
%28 = load i32, i32* %i.ptr 
ret i32 %28 
}

define %class.Main* @Main.getSelf(%class.Main* %self) {
%1 = icmp eq %class.Main* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [83 x i8]
store [83 x i8] c"Segmentation fault : dispatch on null when calling function getSelf on class Main\0a\00", [83 x i8]* %2
%3 = getelementptr inbounds [83 x i8], [83 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret %class.Main* null
cond.false:
%self.ptr = alloca %class.Main* 
store %class.Main* %self, %class.Main** %self.ptr 
%5 = load %class.Main*, %class.Main** %self.ptr 
ret %class.Main* %5 
}

define i32 @Main.truc(%class.Main* %self, i32 %tail, %class.Main* %boolean) {
%1 = icmp eq %class.Main* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [80 x i8]
store [80 x i8] c"Segmentation fault : dispatch on null when calling function truc on class Main\0a\00", [80 x i8]* %2
%3 = getelementptr inbounds [80 x i8], [80 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret i32 0
cond.false:
%self.ptr = alloca %class.Main* 
%tail.ptr = alloca i32 
%boolean.ptr = alloca %class.Main* 
store %class.Main* %self, %class.Main** %self.ptr 
store i32 %tail, i32* %tail.ptr 
store %class.Main* %boolean, %class.Main** %boolean.ptr 
%5 = load i32, i32* %tail.ptr 
%6 = add i32 %5, 3
store i32 %6, i32* %tail.ptr 
%7 = load i32, i32* %tail.ptr 
store i32 %7, i32* %tail.ptr 
ret i32 %7 
}

define %class.X* @.New.X() {
%1 = getelementptr %class.X, %class.X* null, i32 1
%2 = ptrtoint %class.X* %1 to i64
%3 = call i8* @malloc(i64 %2)
%4 = ptrtoint i8* %3 to i64
%5 = inttoptr i64 %4 to %class.X*
%6 = getelementptr %Vtable.X, %Vtable.X* null, i32 1
%7 = ptrtoint %Vtable.X* %6 to i64
%8 = call i8* @malloc(i64 %7)
%9 = ptrtoint i8* %8 to i64
%10 = inttoptr i64 %9 to %Vtable.X*
%11 = getelementptr %class.X, %class.X* %5, i32 0, i32 0 
store %Vtable.X* %10, %Vtable.X** %11 
call void @.Init.X (%class.X* %5)
ret %class.X* %5
}

define void @.Init.X(%class.X* %self) {
%1 = ptrtoint %class.X* %self to i64
%2 = inttoptr i64 %1 to %class.IO*
call void @.Init.IO (%class.IO* %2)
%3 = getelementptr %class.X, %class.X* %self, i32 0, i32 1 
store i32 42, i32* %3 
%4 = getelementptr %class.X, %class.X* %self, i32 0, i32 2 
store i1 1, i1* %4 
%5 = getelementptr %class.X, %class.X* %self, i32 0, i32 0 
%6 = load %Vtable.X*, %Vtable.X** %5 
%7 = getelementptr %Vtable.X, %Vtable.X* %6, i32 0, i32 6 
store i32 (%class.X*)* @X.xfun, i32 (%class.X*)** %7 
ret void
}

define i32 @X.xfun(%class.X* %self) {
%1 = icmp eq %class.X* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [77 x i8]
store [77 x i8] c"Segmentation fault : dispatch on null when calling function xfun on class X\0a\00", [77 x i8]* %2
%3 = getelementptr inbounds [77 x i8], [77 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret i32 0
cond.false:
%self.ptr = alloca %class.X* 
store %class.X* %self, %class.X** %self.ptr 
%5 = getelementptr %class.X, %class.X* %self, i32 0, i32 1 
%6 = load i32, i32* %5 
ret i32 %6 
}

define %class.Y* @.New.Y() {
%1 = getelementptr %class.Y, %class.Y* null, i32 1
%2 = ptrtoint %class.Y* %1 to i64
%3 = call i8* @malloc(i64 %2)
%4 = ptrtoint i8* %3 to i64
%5 = inttoptr i64 %4 to %class.Y*
%6 = getelementptr %Vtable.Y, %Vtable.Y* null, i32 1
%7 = ptrtoint %Vtable.Y* %6 to i64
%8 = call i8* @malloc(i64 %7)
%9 = ptrtoint i8* %8 to i64
%10 = inttoptr i64 %9 to %Vtable.Y*
%11 = getelementptr %class.Y, %class.Y* %5, i32 0, i32 0 
store %Vtable.Y* %10, %Vtable.Y** %11 
call void @.Init.Y (%class.Y* %5)
ret %class.Y* %5
}

define void @.Init.Y(%class.Y* %self) {
%1 = ptrtoint %class.Y* %self to i64
%2 = inttoptr i64 %1 to %class.X*
call void @.Init.X (%class.X* %2)
%3 = getelementptr %class.Y, %class.Y* %self, i32 0, i32 3 
store i32 89, i32* %3 
%4 = getelementptr %class.Y, %class.Y* %self, i32 0, i32 0 
%5 = load %Vtable.Y*, %Vtable.Y** %4 
%6 = getelementptr %Vtable.Y, %Vtable.Y* %5, i32 0, i32 7 
store %class.Y* (%class.Y*)* @Y.getSelf, %class.Y* (%class.Y*)** %6 
%7 = getelementptr %Vtable.Y, %Vtable.Y* %5, i32 0, i32 6 
store i32 (%class.Y*)* @Y.xfun, i32 (%class.Y*)** %7 
%8 = getelementptr %Vtable.Y, %Vtable.Y* %5, i32 0, i32 8 
store i32 (%class.Y*)* @Y.yfun, i32 (%class.Y*)** %8 
ret void
}

define %class.Y* @Y.getSelf(%class.Y* %self) {
%1 = icmp eq %class.Y* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [80 x i8]
store [80 x i8] c"Segmentation fault : dispatch on null when calling function getSelf on class Y\0a\00", [80 x i8]* %2
%3 = getelementptr inbounds [80 x i8], [80 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret %class.Y* null
cond.false:
%self.ptr = alloca %class.Y* 
store %class.Y* %self, %class.Y** %self.ptr 
%5 = load %class.Y*, %class.Y** %self.ptr 
ret %class.Y* %5 
}

define i32 @Y.xfun(%class.Y* %self) {
%1 = icmp eq %class.Y* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [77 x i8]
store [77 x i8] c"Segmentation fault : dispatch on null when calling function xfun on class Y\0a\00", [77 x i8]* %2
%3 = getelementptr inbounds [77 x i8], [77 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret i32 0
cond.false:
%self.ptr = alloca %class.Y* 
store %class.Y* %self, %class.Y** %self.ptr 
ret i32 5 
}

define i32 @Y.yfun(%class.Y* %self) {
%1 = icmp eq %class.Y* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [77 x i8]
store [77 x i8] c"Segmentation fault : dispatch on null when calling function yfun on class Y\0a\00", [77 x i8]* %2
%3 = getelementptr inbounds [77 x i8], [77 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret i32 0
cond.false:
%self.ptr = alloca %class.Y* 
store %class.Y* %self, %class.Y** %self.ptr 
%5 = getelementptr %class.Y, %class.Y* %self, i32 0, i32 3 
%6 = load i32, i32* %5 
%7 = getelementptr %class.Y, %class.Y* %self, i32 0, i32 1 
%8 = load i32, i32* %7 
%9 = getelementptr %class.Y, %class.Y* %self, i32 0, i32 1 
store i32 %8, i32* %9 
%10 = getelementptr %class.Y, %class.Y* %self, i32 0, i32 1 
%11 = load i32, i32* %10 
ret i32 %11 
}

define %class.A* @.New.A() {
%1 = getelementptr %class.A, %class.A* null, i32 1
%2 = ptrtoint %class.A* %1 to i64
%3 = call i8* @malloc(i64 %2)
%4 = ptrtoint i8* %3 to i64
%5 = inttoptr i64 %4 to %class.A*
%6 = getelementptr %Vtable.A, %Vtable.A* null, i32 1
%7 = ptrtoint %Vtable.A* %6 to i64
%8 = call i8* @malloc(i64 %7)
%9 = ptrtoint i8* %8 to i64
%10 = inttoptr i64 %9 to %Vtable.A*
%11 = getelementptr %class.A, %class.A* %5, i32 0, i32 0 
store %Vtable.A* %10, %Vtable.A** %11 
call void @.Init.A (%class.A* %5)
ret %class.A* %5
}

define void @.Init.A(%class.A* %self) {
%1 = ptrtoint %class.A* %self to i64
%2 = inttoptr i64 %1 to %class.Object*
call void @.Init.Object (%class.Object* %2)
%3 = getelementptr %class.A, %class.A* %self, i32 0, i32 0 
%4 = load %Vtable.A*, %Vtable.A** %3 
ret void
}

