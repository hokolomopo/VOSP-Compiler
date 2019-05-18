%Vtable.Parent = type { i8* (%class.Parent*)*}

%class.Parent = type { %Vtable.Parent* }

%Vtable.IO = type { %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.IO*, i1)*, %class.IO* (%class.IO*, i32)*, i8* (%class.IO*)*, i1 (%class.IO*)*, i32 (%class.IO*)*}

%class.IO = type { %Vtable.IO* }

%Vtable.Object = type { }

%class.Object = type { %Vtable.Object* }

%Vtable.Child = type { i8* (%class.Child*)*}

%class.Child = type { %Vtable.Child* }

%Vtable.Main = type { %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.Main*, i1)*, %class.IO* (%class.IO*, i32)*, i8* (%class.IO*)*, i1 (%class.IO*)*, i32 (%class.IO*)*, i32 (%class.Main*)*}

%class.Main = type { %Vtable.Main* }

declare i32 @printf(i8*, ...)
declare i8* @malloc(i64)
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


define %class.IO* @IO.print(%class.IO* %self, i8*) {
  call i32 (i8*, ...) @printf(i8* %0)
  ret %class.IO* %self
}

define %class.IO* @IO.printBool(%class.IO* %self, i1) {
  br i1 %0, label %print_true, label %print_false
  print_true:
    call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @.str.true, i32 0, i32 0))
    br label %end_print
  print_false:
    call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([6 x i8], [6 x i8]* @.str.false, i32 0, i32 0))
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
@.str.true = private unnamed_addr constant [5 x i8] c"true\00"
@.str.false = private unnamed_addr constant [6 x i8] c"false\00"
@.str.inputLine = private unnamed_addr constant [3 x i8] c"%s\00"
@.str.emptyStr = private unnamed_addr constant [1 x i8] c"\00"
@.str.y = private unnamed_addr constant [2 x i8] c"y\00"
@.str.n = private unnamed_addr constant [2 x i8] c"n\00"
@.str.bad_bool = private unnamed_addr constant [43 x i8] c"Boolean input should be either 'y' or 'n'\0a\00"
@.str.bad_int32 = private unnamed_addr constant [50 x i8] c"Error while loading int32 value, maybe too long?\0a\00"


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

@.str = private unnamed_addr constant [8 x i8] c"Parent\0a\00"
@.str.1 = private unnamed_addr constant [7 x i8] c"Child\0a\00"
@.str.2 = private unnamed_addr constant [17 x i8] c"Overriden bitch\0a\00"


define i32 @main () { 
%1 = call %class.Main* @.New.Main()
%returned = call i32 @Main.main(%class.Main* %1)
ret i32 %returned
}

define %class.Parent* @.New.Parent() {
%1 = getelementptr %class.Parent, %class.Parent* null, i32 1
%2 = ptrtoint %class.Parent* %1 to i64
%3 = call i8* @malloc(i64 %2)
%4 = ptrtoint i8* %3 to i64
%5 = inttoptr i64 %4 to %class.Parent*
%6 = getelementptr %Vtable.Parent, %Vtable.Parent* null, i32 1
%7 = ptrtoint %Vtable.Parent* %6 to i64
%8 = call i8* @malloc(i64 %7)
%9 = ptrtoint i8* %8 to i64
%10 = inttoptr i64 %9 to %Vtable.Parent*
%11 = getelementptr %class.Parent, %class.Parent* %5, i32 0, i32 0 
store %Vtable.Parent* %10, %Vtable.Parent** %11 
call void @.Init.Parent (%class.Parent* %5)
ret %class.Parent* %5
}

define void @.Init.Parent(%class.Parent* %self) {
%1 = ptrtoint %class.Parent* %self to i64
%2 = inttoptr i64 %1 to %class.Object*
call void @.Init.Object (%class.Object* %2)
%3 = getelementptr %class.Parent, %class.Parent* %self, i32 0, i32 0 
%4 = load %Vtable.Parent*, %Vtable.Parent** %3 
%5 = getelementptr %Vtable.Parent, %Vtable.Parent* %4, i32 0, i32 0 
store i8* (%class.Parent*)* @Parent.name, i8* (%class.Parent*)** %5 
ret void
}

define i8* @Parent.name(%class.Parent* %self) {
%1 = icmp eq %class.Parent* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [82 x i8]
store [82 x i8] c"Segmentation fault : dispatch on null when calling function name on class Parent\0a\00", [82 x i8]* %2
%3 = getelementptr inbounds [82 x i8], [82 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret i8* getelementptr inbounds ([1 x i8], [1 x i8]* @.str.emptyStr, i32 0, i32 0)
cond.false:
%self.ptr = alloca %class.Parent* 
store %class.Parent* %self, %class.Parent** %self.ptr 
ret i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.str, i32 0, i32 0) 
}

define %class.Child* @.New.Child() {
%1 = getelementptr %class.Child, %class.Child* null, i32 1
%2 = ptrtoint %class.Child* %1 to i64
%3 = call i8* @malloc(i64 %2)
%4 = ptrtoint i8* %3 to i64
%5 = inttoptr i64 %4 to %class.Child*
%6 = getelementptr %Vtable.Child, %Vtable.Child* null, i32 1
%7 = ptrtoint %Vtable.Child* %6 to i64
%8 = call i8* @malloc(i64 %7)
%9 = ptrtoint i8* %8 to i64
%10 = inttoptr i64 %9 to %Vtable.Child*
%11 = getelementptr %class.Child, %class.Child* %5, i32 0, i32 0 
store %Vtable.Child* %10, %Vtable.Child** %11 
call void @.Init.Child (%class.Child* %5)
ret %class.Child* %5
}

define void @.Init.Child(%class.Child* %self) {
%1 = ptrtoint %class.Child* %self to i64
%2 = inttoptr i64 %1 to %class.Parent*
call void @.Init.Parent (%class.Parent* %2)
%3 = getelementptr %class.Child, %class.Child* %self, i32 0, i32 0 
%4 = load %Vtable.Child*, %Vtable.Child** %3 
%5 = getelementptr %Vtable.Child, %Vtable.Child* %4, i32 0, i32 0 
store i8* (%class.Child*)* @Child.name, i8* (%class.Child*)** %5 
ret void
}

define i8* @Child.name(%class.Child* %self) {
%1 = icmp eq %class.Child* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [81 x i8]
store [81 x i8] c"Segmentation fault : dispatch on null when calling function name on class Child\0a\00", [81 x i8]* %2
%3 = getelementptr inbounds [81 x i8], [81 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret i8* getelementptr inbounds ([1 x i8], [1 x i8]* @.str.emptyStr, i32 0, i32 0)
cond.false:
%self.ptr = alloca %class.Child* 
store %class.Child* %self, %class.Child** %self.ptr 
ret i8* getelementptr inbounds ([7 x i8], [7 x i8]* @.str.1, i32 0, i32 0) 
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
%3 = getelementptr %class.Main, %class.Main* %self, i32 0, i32 0 
%4 = load %Vtable.Main*, %Vtable.Main** %3 
%5 = getelementptr %Vtable.Main, %Vtable.Main* %4, i32 0, i32 1 
store %class.IO* (%class.Main*, i1)* @Main.printBool, %class.IO* (%class.Main*, i1)** %5 
%6 = getelementptr %Vtable.Main, %Vtable.Main* %4, i32 0, i32 6 
store i32 (%class.Main*)* @Main.main, i32 (%class.Main*)** %6 
ret void
}

define %class.IO* @Main.printBool(%class.Main* %self, i1 %b) {
%1 = icmp eq %class.Main* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [85 x i8]
store [85 x i8] c"Segmentation fault : dispatch on null when calling function printBool on class Main\0a\00", [85 x i8]* %2
%3 = getelementptr inbounds [85 x i8], [85 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret %class.IO* null
cond.false:
%self.ptr = alloca %class.Main* 
%b.ptr = alloca i1 
store %class.Main* %self, %class.Main** %self.ptr 
store i1 %b, i1* %b.ptr 
%5 = load %class.Main*, %class.Main** %self.ptr 
%6 = ptrtoint %class.Main* %5 to i64
%7 = inttoptr i64 %6 to %class.IO*
%8 = getelementptr %class.IO, %class.IO* %7, i32 0, i32 0 
%9 = load %Vtable.IO*, %Vtable.IO** %8 
%10 = getelementptr %Vtable.IO, %Vtable.IO* %9, i32 0, i32 0 
%11 = load %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.IO*, i8*)** %10 
%12 = call %class.IO* %11(%class.IO* %7, i8* getelementptr inbounds ([17 x i8], [17 x i8]* @.str.2, i32 0, i32 0))
ret %class.IO* %12 
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
%6 = getelementptr %class.Main, %class.Main* %5, i32 0, i32 0 
%7 = load %Vtable.Main*, %Vtable.Main** %6 
%8 = getelementptr %Vtable.Main, %Vtable.Main* %7, i32 0, i32 1 
%9 = load %class.IO* (%class.Main*, i1)*, %class.IO* (%class.Main*, i1)** %8 
%10 = call %class.IO* %9(%class.Main* %5, i1 1)
%11 = load %class.Main*, %class.Main** %self.ptr 
%12 = ptrtoint %class.Main* %11 to i64
%13 = inttoptr i64 %12 to %class.IO*
%cond2.ptr = alloca %class.Parent* 
br i1 1, label %cond2.true, label %cond2.false

cond2.true:
%14 = call %class.Parent* @.New.Parent()
store %class.Parent* %14, %class.Parent** %cond2.ptr 
br label %cond2.end

cond2.false:
%15 = call %class.Child* @.New.Child()
%16 = ptrtoint %class.Child* %15 to i64
%17 = inttoptr i64 %16 to %class.Parent*
store %class.Parent* %17, %class.Parent** %cond2.ptr 
br label %cond2.end

cond2.end:
%18 = load %class.Parent*, %class.Parent** %cond2.ptr 
%19 = getelementptr %class.Parent, %class.Parent* %18, i32 0, i32 0 
%20 = load %Vtable.Parent*, %Vtable.Parent** %19 
%21 = getelementptr %Vtable.Parent, %Vtable.Parent* %20, i32 0, i32 0 
%22 = load i8* (%class.Parent*)*, i8* (%class.Parent*)** %21 
%23 = call i8* %22(%class.Parent* %18)
%24 = getelementptr %class.IO, %class.IO* %13, i32 0, i32 0 
%25 = load %Vtable.IO*, %Vtable.IO** %24 
%26 = getelementptr %Vtable.IO, %Vtable.IO* %25, i32 0, i32 0 
%27 = load %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.IO*, i8*)** %26 
%28 = call %class.IO* %27(%class.IO* %13, i8* %23)
%29 = load %class.Main*, %class.Main** %self.ptr 
%30 = ptrtoint %class.Main* %29 to i64
%31 = inttoptr i64 %30 to %class.IO*
%cond3.ptr = alloca %class.Parent* 
br i1 0, label %cond3.true, label %cond3.false

cond3.true:
%32 = call %class.Parent* @.New.Parent()
store %class.Parent* %32, %class.Parent** %cond3.ptr 
br label %cond3.end

cond3.false:
%33 = call %class.Child* @.New.Child()
%34 = ptrtoint %class.Child* %33 to i64
%35 = inttoptr i64 %34 to %class.Parent*
store %class.Parent* %35, %class.Parent** %cond3.ptr 
br label %cond3.end

cond3.end:
%36 = load %class.Parent*, %class.Parent** %cond3.ptr 
%37 = getelementptr %class.Parent, %class.Parent* %36, i32 0, i32 0 
%38 = load %Vtable.Parent*, %Vtable.Parent** %37 
%39 = getelementptr %Vtable.Parent, %Vtable.Parent* %38, i32 0, i32 0 
%40 = load i8* (%class.Parent*)*, i8* (%class.Parent*)** %39 
%41 = call i8* %40(%class.Parent* %36)
%42 = getelementptr %class.IO, %class.IO* %31, i32 0, i32 0 
%43 = load %Vtable.IO*, %Vtable.IO** %42 
%44 = getelementptr %Vtable.IO, %Vtable.IO* %43, i32 0, i32 0 
%45 = load %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.IO*, i8*)** %44 
%46 = call %class.IO* %45(%class.IO* %31, i8* %41)
ret i32 0 
}

