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
@.str = private unnamed_addr constant [46 x i8] c"Enter an integer greater-than or equal to 0: \00"
@.str.1 = private unnamed_addr constant [51 x i8] c"Error: number must be greater-than or equal to 0.\0a\00"
@.str.2 = private unnamed_addr constant [18 x i8] c"The factorial of \00"
@.str.3 = private unnamed_addr constant [5 x i8] c" is \00"
@.str.4 = private unnamed_addr constant [2 x i8] c"\0a\00"


define i32 @main () { 
%1 = call %class.Main* @.New.Main()
%returned = call i32 @Main.main(%class.Main* %1)
ret i32 %returned
}

%class.Main = type { i8* }

define i32 @Main.factorial(%class.Main* %self, i32 %n) {
%self.ptr = alloca %class.Main* 
%n.ptr = alloca i32 
store %class.Main* %self, %class.Main** %self.ptr 
store i32 %n, i32* %n.ptr 
%cond.ptr = alloca i32 
%1 = load i32, i32* %n.ptr 
%2 = icmp slt i32 %1, 2
br i1 %2, label %cond.true, label %cond.false

cond.true:
store i32 1, i32* %cond.ptr 
br label %cond.end

cond.false:
%3 = load i32, i32* %n.ptr 
%4 = load %class.Main*, %class.Main** %self.ptr 
%5 = load i32, i32* %n.ptr 
%6 = sub i32 %5, 1
%7 = call i32 @Main.factorial(%class.Main* %4, i32 %6)
%8 = mul i32 %3, %7
store i32 %8, i32* %cond.ptr 
br label %cond.end

cond.end:
%9 = load i32, i32* %cond.ptr 
ret i32 %9 
}

define i32 @Main.main(%class.Main* %self) {
%self.ptr = alloca %class.Main* 
store %class.Main* %self, %class.Main** %self.ptr 
%1 = load %class.Main*, %class.Main** %self.ptr 
%2 = ptrtoint %class.Main* %1 to i64
%3 = inttoptr i64 %2 to %class.IO*
%4 = call %class.IO* @IO.print(%class.IO* %3, i8* getelementptr inbounds ([46 x i8], [46 x i8]* @.str, i32 0, i32 0))
%n.ptr = alloca i32 
%5 = load %class.Main*, %class.Main** %self.ptr 
%6 = ptrtoint %class.Main* %5 to i64
%7 = inttoptr i64 %6 to %class.IO*
%8 = call i32 @IO.inputInt32(%class.IO* %7)
store i32 %8, i32* %n.ptr 
%cond.ptr = alloca i32 
%9 = load i32, i32* %n.ptr 
%10 = icmp slt i32 %9, 0
br i1 %10, label %cond.true, label %cond.false

cond.true:
%11 = load %class.Main*, %class.Main** %self.ptr 
%12 = ptrtoint %class.Main* %11 to i64
%13 = inttoptr i64 %12 to %class.IO*
%14 = call %class.IO* @IO.print(%class.IO* %13, i8* getelementptr inbounds ([51 x i8], [51 x i8]* @.str.1, i32 0, i32 0))
%15 = sub i32 0, 1
store i32 %15, i32* %cond.ptr 
br label %cond.end

cond.false:
%16 = load %class.Main*, %class.Main** %self.ptr 
%17 = ptrtoint %class.Main* %16 to i64
%18 = inttoptr i64 %17 to %class.IO*
%19 = call %class.IO* @IO.print(%class.IO* %18, i8* getelementptr inbounds ([18 x i8], [18 x i8]* @.str.2, i32 0, i32 0))
%20 = load i32, i32* %n.ptr 
%21 = call %class.IO* @IO.printInt32(%class.IO* %19, i32 %20)
%22 = call %class.IO* @IO.print(%class.IO* %21, i8* getelementptr inbounds ([5 x i8], [5 x i8]* @.str.3, i32 0, i32 0))
%23 = load %class.Main*, %class.Main** %self.ptr 
%24 = ptrtoint %class.Main* %23 to i64
%25 = inttoptr i64 %24 to %class.IO*
%26 = load %class.Main*, %class.Main** %self.ptr 
%27 = load i32, i32* %n.ptr 
%28 = call i32 @Main.factorial(%class.Main* %26, i32 %27)
%29 = call %class.IO* @IO.printInt32(%class.IO* %25, i32 %28)
%30 = call %class.IO* @IO.print(%class.IO* %29, i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.4, i32 0, i32 0))
store i32 0, i32* %cond.ptr 
br label %cond.end

cond.end:
%31 = load i32, i32* %cond.ptr 
ret i32 %31 
}


define %class.Main* @.New.Main() {
%1 = getelementptr %class.Main, %class.Main* null, i32 1
%2 = ptrtoint %class.Main* %1 to i64
%3 = call i8* @malloc(i64 %2)
%4 = ptrtoint i8* %3 to i64
%5 = inttoptr i64 %4 to %class.Main*
%6 = getelementptr %class.Main, %class.Main* %5, i32 0, i32 0 
store i8* getelementptr inbounds ([5 x i8], [5 x i8]* null, i32 0, i32 0), i8** %6 
ret %class.Main* %5
}

