%Vtable.Nil = type { i1 (%class.List*)*, i32 (%class.List*)*}

%class.Nil = type { %Vtable.Nil* }

%Vtable.Cons = type { i1 (%class.Cons*)*, i32 (%class.Cons*)*, %class.Cons* (%class.Cons*, i32, %class.List*)*, i32 (%class.Cons*)*}

%class.Cons = type { %Vtable.Cons*, i32, %class.List* }

%Vtable.IO = type { %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.IO*, i1)*, %class.IO* (%class.IO*, i32)*, i8* (%class.IO*)*, i1 (%class.IO*)*, i32 (%class.IO*)*}

%class.IO = type { %Vtable.IO* }

%Vtable.Object = type { }

%class.Object = type { %Vtable.Object* }

%Vtable.List = type { i1 (%class.List*)*, i32 (%class.List*)*}

%class.List = type { %Vtable.List* }

%Vtable.Main = type { %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.IO*, i1)*, %class.IO* (%class.IO*, i32)*, i8* (%class.IO*)*, i1 (%class.IO*)*, i32 (%class.IO*)*, i32 (%class.Main*)*}

%class.Main = type { %Vtable.Main*, i32 }

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

@.str = private unnamed_addr constant [17 x i8] c"List has length \00"
@.str.1 = private unnamed_addr constant [2 x i8] c"\0a\00"


define i32 @main () { 
%1 = call %class.Main* @.New.Main()
%returned = call i32 @Main.main(%class.Main* %1)
ret i32 %returned
}

define %class.List* @.New.List() {
%1 = getelementptr %class.List, %class.List* null, i32 1
%2 = ptrtoint %class.List* %1 to i64
%3 = call i8* @malloc(i64 %2)
%4 = ptrtoint i8* %3 to i64
%5 = inttoptr i64 %4 to %class.List*
%6 = getelementptr %Vtable.List, %Vtable.List* null, i32 1
%7 = ptrtoint %Vtable.List* %6 to i64
%8 = call i8* @malloc(i64 %7)
%9 = ptrtoint i8* %8 to i64
%10 = inttoptr i64 %9 to %Vtable.List*
%11 = getelementptr %class.List, %class.List* %5, i32 0, i32 0 
store %Vtable.List* %10, %Vtable.List** %11 
call void @.Init.List (%class.List* %5)
ret %class.List* %5
}

define void @.Init.List(%class.List* %self) {
%1 = ptrtoint %class.List* %self to i64
%2 = inttoptr i64 %1 to %class.Object*
call void @.Init.Object (%class.Object* %2)
%3 = getelementptr %class.List, %class.List* %self, i32 0, i32 0 
%4 = load %Vtable.List*, %Vtable.List** %3 
%5 = getelementptr %Vtable.List, %Vtable.List* %4, i32 0, i32 0 
store i1 (%class.List*)* @List.isNil, i1 (%class.List*)** %5 
%6 = getelementptr %Vtable.List, %Vtable.List* %4, i32 0, i32 1 
store i32 (%class.List*)* @List.length, i32 (%class.List*)** %6 
ret void
}

define i1 @List.isNil(%class.List* %self) {
%1 = icmp eq %class.List* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [81 x i8]
store [81 x i8] c"Segmentation fault : dispatch on null when calling function isNil on class List\0a\00", [81 x i8]* %2
%3 = getelementptr inbounds [81 x i8], [81 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret i1 0
cond.false:
%self.ptr = alloca %class.List* 
store %class.List* %self, %class.List** %self.ptr 
ret i1 1 
}

define i32 @List.length(%class.List* %self) {
%1 = icmp eq %class.List* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [82 x i8]
store [82 x i8] c"Segmentation fault : dispatch on null when calling function length on class List\0a\00", [82 x i8]* %2
%3 = getelementptr inbounds [82 x i8], [82 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret i32 0
cond.false:
%self.ptr = alloca %class.List* 
store %class.List* %self, %class.List** %self.ptr 
ret i32 0 
}

define %class.Nil* @.New.Nil() {
%1 = getelementptr %class.Nil, %class.Nil* null, i32 1
%2 = ptrtoint %class.Nil* %1 to i64
%3 = call i8* @malloc(i64 %2)
%4 = ptrtoint i8* %3 to i64
%5 = inttoptr i64 %4 to %class.Nil*
%6 = getelementptr %Vtable.Nil, %Vtable.Nil* null, i32 1
%7 = ptrtoint %Vtable.Nil* %6 to i64
%8 = call i8* @malloc(i64 %7)
%9 = ptrtoint i8* %8 to i64
%10 = inttoptr i64 %9 to %Vtable.Nil*
%11 = getelementptr %class.Nil, %class.Nil* %5, i32 0, i32 0 
store %Vtable.Nil* %10, %Vtable.Nil** %11 
call void @.Init.Nil (%class.Nil* %5)
ret %class.Nil* %5
}

define void @.Init.Nil(%class.Nil* %self) {
%1 = ptrtoint %class.Nil* %self to i64
%2 = inttoptr i64 %1 to %class.List*
call void @.Init.List (%class.List* %2)
%3 = getelementptr %class.Nil, %class.Nil* %self, i32 0, i32 0 
%4 = load %Vtable.Nil*, %Vtable.Nil** %3 
ret void
}

define %class.Cons* @.New.Cons() {
%1 = getelementptr %class.Cons, %class.Cons* null, i32 1
%2 = ptrtoint %class.Cons* %1 to i64
%3 = call i8* @malloc(i64 %2)
%4 = ptrtoint i8* %3 to i64
%5 = inttoptr i64 %4 to %class.Cons*
%6 = getelementptr %Vtable.Cons, %Vtable.Cons* null, i32 1
%7 = ptrtoint %Vtable.Cons* %6 to i64
%8 = call i8* @malloc(i64 %7)
%9 = ptrtoint i8* %8 to i64
%10 = inttoptr i64 %9 to %Vtable.Cons*
%11 = getelementptr %class.Cons, %class.Cons* %5, i32 0, i32 0 
store %Vtable.Cons* %10, %Vtable.Cons** %11 
call void @.Init.Cons (%class.Cons* %5)
ret %class.Cons* %5
}

define void @.Init.Cons(%class.Cons* %self) {
%1 = ptrtoint %class.Cons* %self to i64
%2 = inttoptr i64 %1 to %class.List*
call void @.Init.List (%class.List* %2)
%3 = getelementptr %class.Cons, %class.Cons* %self, i32 0, i32 1 
store i32 0, i32* %3 
%4 = getelementptr %class.Cons, %class.Cons* %self, i32 0, i32 2 
store %class.List* null, %class.List** %4 
%5 = getelementptr %class.Cons, %class.Cons* %self, i32 0, i32 0 
%6 = load %Vtable.Cons*, %Vtable.Cons** %5 
%7 = getelementptr %Vtable.Cons, %Vtable.Cons* %6, i32 0, i32 2 
store %class.Cons* (%class.Cons*, i32, %class.List*)* @Cons.init, %class.Cons* (%class.Cons*, i32, %class.List*)** %7 
%8 = getelementptr %Vtable.Cons, %Vtable.Cons* %6, i32 0, i32 3 
store i32 (%class.Cons*)* @Cons.head, i32 (%class.Cons*)** %8 
%9 = getelementptr %Vtable.Cons, %Vtable.Cons* %6, i32 0, i32 0 
store i1 (%class.Cons*)* @Cons.isNil, i1 (%class.Cons*)** %9 
%10 = getelementptr %Vtable.Cons, %Vtable.Cons* %6, i32 0, i32 1 
store i32 (%class.Cons*)* @Cons.length, i32 (%class.Cons*)** %10 
ret void
}

define %class.Cons* @Cons.init(%class.Cons* %self, i32 %hd, %class.List* %tl) {
%1 = icmp eq %class.Cons* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [80 x i8]
store [80 x i8] c"Segmentation fault : dispatch on null when calling function init on class Cons\0a\00", [80 x i8]* %2
%3 = getelementptr inbounds [80 x i8], [80 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret %class.Cons* null
cond.false:
%self.ptr = alloca %class.Cons* 
%hd.ptr = alloca i32 
%tl.ptr = alloca %class.List* 
store %class.Cons* %self, %class.Cons** %self.ptr 
store i32 %hd, i32* %hd.ptr 
store %class.List* %tl, %class.List** %tl.ptr 
%5 = load i32, i32* %hd.ptr 
%6 = getelementptr %class.Cons, %class.Cons* %self, i32 0, i32 1 
store i32 %5, i32* %6 
%7 = load %class.List*, %class.List** %tl.ptr 
%8 = getelementptr %class.Cons, %class.Cons* %self, i32 0, i32 2 
store %class.List* %7, %class.List** %8 
%9 = load %class.Cons*, %class.Cons** %self.ptr 
ret %class.Cons* %9 
}

define i32 @Cons.head(%class.Cons* %self) {
%1 = icmp eq %class.Cons* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [80 x i8]
store [80 x i8] c"Segmentation fault : dispatch on null when calling function head on class Cons\0a\00", [80 x i8]* %2
%3 = getelementptr inbounds [80 x i8], [80 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret i32 0
cond.false:
%self.ptr = alloca %class.Cons* 
store %class.Cons* %self, %class.Cons** %self.ptr 
%5 = getelementptr %class.Cons, %class.Cons* %self, i32 0, i32 1 
%6 = load i32, i32* %5 
ret i32 %6 
}

define i1 @Cons.isNil(%class.Cons* %self) {
%1 = icmp eq %class.Cons* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [81 x i8]
store [81 x i8] c"Segmentation fault : dispatch on null when calling function isNil on class Cons\0a\00", [81 x i8]* %2
%3 = getelementptr inbounds [81 x i8], [81 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret i1 0
cond.false:
%self.ptr = alloca %class.Cons* 
store %class.Cons* %self, %class.Cons** %self.ptr 
ret i1 0 
}

define i32 @Cons.length(%class.Cons* %self) {
%1 = icmp eq %class.Cons* null, %self
br i1 %1, label %cond.true, label %cond.false
cond.true:
%2 = alloca [82 x i8]
store [82 x i8] c"Segmentation fault : dispatch on null when calling function length on class Cons\0a\00", [82 x i8]* %2
%3 = getelementptr inbounds [82 x i8], [82 x i8]* %2, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %3)
call void @exit(i32 -1)
ret i32 0
cond.false:
%self.ptr = alloca %class.Cons* 
store %class.Cons* %self, %class.Cons** %self.ptr 
%5 = getelementptr %class.Cons, %class.Cons* %self, i32 0, i32 2 
%6 = load %class.List*, %class.List** %5 
%7 = getelementptr %class.List, %class.List* %6, i32 0, i32 0 
%8 = load %Vtable.List*, %Vtable.List** %7 
%9 = getelementptr %Vtable.List, %Vtable.List* %8, i32 0, i32 1 
%10 = load i32 (%class.List*)*, i32 (%class.List*)** %9 
%11 = call i32 %10(%class.List* %6)
%12 = add i32 1, %11
ret i32 %12 
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
store i32 457, i32* %3 
%4 = getelementptr %class.Main, %class.Main* %self, i32 0, i32 0 
%5 = load %Vtable.Main*, %Vtable.Main** %4 
%6 = getelementptr %Vtable.Main, %Vtable.Main* %5, i32 0, i32 6 
store i32 (%class.Main*)* @Main.main, i32 (%class.Main*)** %6 
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
%5 = alloca %class.List* 
%6 = call %class.Cons* @.New.Cons()
%7 = call %class.Cons* @.New.Cons()
%8 = call %class.Cons* @.New.Cons()
%9 = call %class.Nil* @.New.Nil()
%10 = ptrtoint %class.Nil* %9 to i64
%11 = inttoptr i64 %10 to %class.List*
%12 = getelementptr %class.Cons, %class.Cons* %8, i32 0, i32 0 
%13 = load %Vtable.Cons*, %Vtable.Cons** %12 
%14 = getelementptr %Vtable.Cons, %Vtable.Cons* %13, i32 0, i32 2 
%15 = load %class.Cons* (%class.Cons*, i32, %class.List*)*, %class.Cons* (%class.Cons*, i32, %class.List*)** %14 
%16 = call %class.Cons* %15(%class.Cons* %8, i32 2, %class.List* %11)
%17 = ptrtoint %class.Cons* %16 to i64
%18 = inttoptr i64 %17 to %class.List*
%19 = getelementptr %class.Cons, %class.Cons* %7, i32 0, i32 0 
%20 = load %Vtable.Cons*, %Vtable.Cons** %19 
%21 = getelementptr %Vtable.Cons, %Vtable.Cons* %20, i32 0, i32 2 
%22 = load %class.Cons* (%class.Cons*, i32, %class.List*)*, %class.Cons* (%class.Cons*, i32, %class.List*)** %21 
%23 = call %class.Cons* %22(%class.Cons* %7, i32 1, %class.List* %18)
%24 = ptrtoint %class.Cons* %23 to i64
%25 = inttoptr i64 %24 to %class.List*
%26 = getelementptr %class.Cons, %class.Cons* %6, i32 0, i32 0 
%27 = load %Vtable.Cons*, %Vtable.Cons** %26 
%28 = getelementptr %Vtable.Cons, %Vtable.Cons* %27, i32 0, i32 2 
%29 = load %class.Cons* (%class.Cons*, i32, %class.List*)*, %class.Cons* (%class.Cons*, i32, %class.List*)** %28 
%30 = call %class.Cons* %29(%class.Cons* %6, i32 0, %class.List* %25)
%31 = ptrtoint %class.Cons* %30 to i64
%32 = inttoptr i64 %31 to %class.List*
store %class.List* %32, %class.List** %5 
%33 = load %class.Main*, %class.Main** %self.ptr 
%34 = ptrtoint %class.Main* %33 to i64
%35 = inttoptr i64 %34 to %class.IO*
%36 = getelementptr %class.IO, %class.IO* %35, i32 0, i32 0 
%37 = load %Vtable.IO*, %Vtable.IO** %36 
%38 = getelementptr %Vtable.IO, %Vtable.IO* %37, i32 0, i32 0 
%39 = load %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.IO*, i8*)** %38 
%40 = call %class.IO* %39(%class.IO* %35, i8* getelementptr inbounds ([17 x i8], [17 x i8]* @.str, i32 0, i32 0))
%41 = load %class.Main*, %class.Main** %self.ptr 
%42 = ptrtoint %class.Main* %41 to i64
%43 = inttoptr i64 %42 to %class.IO*
%44 = load %class.List*, %class.List** %5 
%45 = getelementptr %class.List, %class.List* %44, i32 0, i32 0 
%46 = load %Vtable.List*, %Vtable.List** %45 
%47 = getelementptr %Vtable.List, %Vtable.List* %46, i32 0, i32 1 
%48 = load i32 (%class.List*)*, i32 (%class.List*)** %47 
%49 = call i32 %48(%class.List* %44)
%50 = getelementptr %class.IO, %class.IO* %43, i32 0, i32 0 
%51 = load %Vtable.IO*, %Vtable.IO** %50 
%52 = getelementptr %Vtable.IO, %Vtable.IO* %51, i32 0, i32 2 
%53 = load %class.IO* (%class.IO*, i32)*, %class.IO* (%class.IO*, i32)** %52 
%54 = call %class.IO* %53(%class.IO* %43, i32 %49)
%55 = load %class.Main*, %class.Main** %self.ptr 
%56 = ptrtoint %class.Main* %55 to i64
%57 = inttoptr i64 %56 to %class.IO*
%58 = getelementptr %class.IO, %class.IO* %57, i32 0, i32 0 
%59 = load %Vtable.IO*, %Vtable.IO** %58 
%60 = getelementptr %Vtable.IO, %Vtable.IO* %59, i32 0, i32 0 
%61 = load %class.IO* (%class.IO*, i8*)*, %class.IO* (%class.IO*, i8*)** %60 
%62 = call %class.IO* %61(%class.IO* %57, i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.1, i32 0, i32 0))
ret i32 0 
}

