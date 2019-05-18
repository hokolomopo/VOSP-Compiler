declare i32 @printf(i8*, ...)
declare i8* @malloc(i64)
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

%class.Object = type {  }

define %class.Object* @.New.Object() {
%1 = getelementptr %class.Object, %class.Object* null, i32 1
%2 = ptrtoint %class.Object* %1 to i64
%3 = call i8* @malloc(i64 %2)
%4 = ptrtoint i8* %3 to i64
%5 = inttoptr i64 %4 to %class.Object*
ret %class.Object* %5
}
@.str = private unnamed_addr constant [4 x i8] c"OK\0a\00"
@.str.1 = private unnamed_addr constant [4 x i8] c"KO\0a\00"
@.str.2 = private unnamed_addr constant [4 x i8] c"KO\0a\00"
@.str.3 = private unnamed_addr constant [4 x i8] c"OK\0a\00"
@.str.4 = private unnamed_addr constant [4 x i8] c"KO\0a\00"
@.str.5 = private unnamed_addr constant [4 x i8] c"OK\0a\00"
@.str.6 = private unnamed_addr constant [4 x i8] c"KO\0a\00"
@.str.7 = private unnamed_addr constant [4 x i8] c"OK\0a\00"
@.str.8 = private unnamed_addr constant [4 x i8] c"OK\0a\00"
@.str.9 = private unnamed_addr constant [4 x i8] c"KO\0a\00"
@.str.10 = private unnamed_addr constant [4 x i8] c"OK\0a\00"
@.str.11 = private unnamed_addr constant [4 x i8] c"KO\0a\00"


define i32 @main () { 
%1 = call %class.Main* @.New.Main()
%returned = call i32 @Main.main(%class.Main* %1)
ret i32 %returned
}

%class.Main = type { i8* }

define i32 @Main.main(%class.Main* %self) {
%self.ptr = alloca %class.Main* 
store %class.Main* %self, %class.Main** %self.ptr 
%cond.ptr = alloca %class.IO* 
%1 = load %class.Main*, %class.Main** %self.ptr 
%2 = load %class.Main*, %class.Main** %self.ptr 
%3 = ptrtoint %class.Main* %1 to i64
%4 = ptrtoint %class.Main* %2 to i64
%5 = icmp eq i64 %3, %4
br i1 %5, label %cond.true, label %cond.false

cond.true:
%6 = load %class.Main*, %class.Main** %self.ptr 
%7 = ptrtoint %class.Main* %6 to i64
%8 = inttoptr i64 %7 to %class.IO*
%9 = call %class.IO* @IO.print(%class.IO* %8, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str, i32 0, i32 0))
store %class.IO* %9, %class.IO** %cond.ptr 
br label %cond.end

cond.false:
%10 = load %class.Main*, %class.Main** %self.ptr 
%11 = ptrtoint %class.Main* %10 to i64
%12 = inttoptr i64 %11 to %class.IO*
%13 = call %class.IO* @IO.print(%class.IO* %12, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.1, i32 0, i32 0))
store %class.IO* %13, %class.IO** %cond.ptr 
br label %cond.end

cond.end:
%14 = load %class.IO*, %class.IO** %cond.ptr 
%cond2.ptr = alloca %class.IO* 
%15 = load %class.Main*, %class.Main** %self.ptr 
%16 = call %class.Main* @.New.Main()
%17 = ptrtoint %class.Main* %15 to i64
%18 = ptrtoint %class.Main* %16 to i64
%19 = icmp eq i64 %17, %18
br i1 %19, label %cond2.true, label %cond2.false

cond2.true:
%20 = load %class.Main*, %class.Main** %self.ptr 
%21 = ptrtoint %class.Main* %20 to i64
%22 = inttoptr i64 %21 to %class.IO*
%23 = call %class.IO* @IO.print(%class.IO* %22, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.2, i32 0, i32 0))
store %class.IO* %23, %class.IO** %cond2.ptr 
br label %cond2.end

cond2.false:
%24 = load %class.Main*, %class.Main** %self.ptr 
%25 = ptrtoint %class.Main* %24 to i64
%26 = inttoptr i64 %25 to %class.IO*
%27 = call %class.IO* @IO.print(%class.IO* %26, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.3, i32 0, i32 0))
store %class.IO* %27, %class.IO** %cond2.ptr 
br label %cond2.end

cond2.end:
%28 = load %class.IO*, %class.IO** %cond2.ptr 
%29 = alloca %class.Object* 
%cond3.ptr = alloca %class.IO* 
%30 = load %class.Object*, %class.Object** %29 
%31 = load %class.Main*, %class.Main** %self.ptr 
%32 = ptrtoint %class.Object* %30 to i64
%33 = ptrtoint %class.Main* %31 to i64
%34 = icmp eq i64 %32, %33
br i1 %34, label %cond3.true, label %cond3.false

cond3.true:
%35 = load %class.Main*, %class.Main** %self.ptr 
%36 = ptrtoint %class.Main* %35 to i64
%37 = inttoptr i64 %36 to %class.IO*
%38 = call %class.IO* @IO.print(%class.IO* %37, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.4, i32 0, i32 0))
store %class.IO* %38, %class.IO** %cond3.ptr 
br label %cond3.end

cond3.false:
%39 = load %class.Main*, %class.Main** %self.ptr 
%40 = ptrtoint %class.Main* %39 to i64
%41 = inttoptr i64 %40 to %class.IO*
%42 = call %class.IO* @IO.print(%class.IO* %41, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.5, i32 0, i32 0))
store %class.IO* %42, %class.IO** %cond3.ptr 
br label %cond3.end

cond3.end:
%43 = load %class.IO*, %class.IO** %cond3.ptr 
%cond4.ptr = alloca %class.IO* 
%44 = load %class.Main*, %class.Main** %self.ptr 
%45 = load %class.Object*, %class.Object** %29 
%46 = ptrtoint %class.Main* %44 to i64
%47 = ptrtoint %class.Object* %45 to i64
%48 = icmp eq i64 %46, %47
br i1 %48, label %cond4.true, label %cond4.false

cond4.true:
%49 = load %class.Main*, %class.Main** %self.ptr 
%50 = ptrtoint %class.Main* %49 to i64
%51 = inttoptr i64 %50 to %class.IO*
%52 = call %class.IO* @IO.print(%class.IO* %51, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.6, i32 0, i32 0))
store %class.IO* %52, %class.IO** %cond4.ptr 
br label %cond4.end

cond4.false:
%53 = load %class.Main*, %class.Main** %self.ptr 
%54 = ptrtoint %class.Main* %53 to i64
%55 = inttoptr i64 %54 to %class.IO*
%56 = call %class.IO* @IO.print(%class.IO* %55, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.7, i32 0, i32 0))
store %class.IO* %56, %class.IO** %cond4.ptr 
br label %cond4.end

cond4.end:
%57 = load %class.IO*, %class.IO** %cond4.ptr 
%58 = load %class.Main*, %class.Main** %self.ptr 
%59 = ptrtoint %class.Main* %58 to i64
%60 = inttoptr i64 %59 to %class.Object*
store %class.Object* %60, %class.Object** %29 
%cond5.ptr = alloca %class.IO* 
%61 = load %class.Object*, %class.Object** %29 
%62 = load %class.Main*, %class.Main** %self.ptr 
%63 = ptrtoint %class.Object* %61 to i64
%64 = ptrtoint %class.Main* %62 to i64
%65 = icmp eq i64 %63, %64
br i1 %65, label %cond5.true, label %cond5.false

cond5.true:
%66 = load %class.Main*, %class.Main** %self.ptr 
%67 = ptrtoint %class.Main* %66 to i64
%68 = inttoptr i64 %67 to %class.IO*
%69 = call %class.IO* @IO.print(%class.IO* %68, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.8, i32 0, i32 0))
store %class.IO* %69, %class.IO** %cond5.ptr 
br label %cond5.end

cond5.false:
%70 = load %class.Main*, %class.Main** %self.ptr 
%71 = ptrtoint %class.Main* %70 to i64
%72 = inttoptr i64 %71 to %class.IO*
%73 = call %class.IO* @IO.print(%class.IO* %72, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.9, i32 0, i32 0))
store %class.IO* %73, %class.IO** %cond5.ptr 
br label %cond5.end

cond5.end:
%74 = load %class.IO*, %class.IO** %cond5.ptr 
%cond6.ptr = alloca %class.IO* 
%75 = load %class.Main*, %class.Main** %self.ptr 
%76 = load %class.Object*, %class.Object** %29 
%77 = ptrtoint %class.Main* %75 to i64
%78 = ptrtoint %class.Object* %76 to i64
%79 = icmp eq i64 %77, %78
br i1 %79, label %cond6.true, label %cond6.false

cond6.true:
%80 = load %class.Main*, %class.Main** %self.ptr 
%81 = ptrtoint %class.Main* %80 to i64
%82 = inttoptr i64 %81 to %class.IO*
%83 = call %class.IO* @IO.print(%class.IO* %82, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.10, i32 0, i32 0))
store %class.IO* %83, %class.IO** %cond6.ptr 
br label %cond6.end

cond6.false:
%84 = load %class.Main*, %class.Main** %self.ptr 
%85 = ptrtoint %class.Main* %84 to i64
%86 = inttoptr i64 %85 to %class.IO*
%87 = call %class.IO* @IO.print(%class.IO* %86, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.11, i32 0, i32 0))
store %class.IO* %87, %class.IO** %cond6.ptr 
br label %cond6.end

cond6.end:
%88 = load %class.IO*, %class.IO** %cond6.ptr 
ret i32 0 
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

