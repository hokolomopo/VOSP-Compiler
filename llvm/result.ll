@.str = private unnamed_addr constant [38 : i8] c"Viva la vida baby and keep on Rockin'\00"
@.str.1 = private unnamed_addr constant [4 : i8] c"lol\00"

%class.Main = type { int32, int32 }

define i32 @Main.main(%class.Main* %self) {
%selfPtr = alloca %class.Main* 
store %class.Main* %self, %class.Main** %selfPtr 
%nPtr = alloca i32 
store i32 3, i32* %nPtr 
%condPtr = alloca i32 
%0 = load i32, i32* %nPtr 
%1 = TODO binop
br i1 %1, label %cond.true, label %cond.false

cond.true:
%2 = sub i32 0, 1
store i32 %2, i32* %condPtr 
br label %cond.end

cond.false:
store i32 897, i32* %condPtr 
br label %cond.end

cond.end:
%cond = load i32, i32* %condPtr 
ret i32 %cond 
}

define i8* @Main.testString(%class.Main* %self, i8* %myString) {
%selfPtr = alloca %class.Main* 
%myStringPtr = alloca i8* 
store %class.Main* %self, %class.Main** %selfPtr 
store i8* %myString, i8** %myStringPtr 
store i8* getelementptr inbounds ([38 x i8], [38 x i8]* @.str, i32 0, i32 0), i8** %myStringPtr 
ret i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.1, i32 0, i32 0) 
}

define i32 @Main.truc(%class.Main* %self, i32 %tail, %class.Main* %boolean) {
%selfPtr = alloca %class.Main* 
%tailPtr = alloca i32 
%booleanPtr = alloca %class.Main* 
store %class.Main* %self, %class.Main** %selfPtr 
store i32 %tail, i32* %tailPtr 
store %class.Main* %boolean, %class.Main** %booleanPtr 
%head = getelementptr %class.Main, %class.Main* %self, i32 0, i32 0 
%0 = load i32, i32* %head 
%1 = load i32, i32* %tailPtr 
%2 = mul i32 %0, %1
%3 = add i32 2, %2
%head = getelementptr %class.Main, %class.Main* %self, i32 0, i32 0 
store i32 %3, i32* %head 
%xsPtr = alloca i32 
%4 = load i32, i32* %tailPtr 
store i32 %4, i32* %xsPtr 
%5 = load i32, i32* %xsPtr 
store i32 %5, i32* %tailPtr 
loop.cond:
br i1 true, label %loop.start, label %loop.end

loop.start:
%6 = load i32, i32* %tailPtr 
%7 = add i32 %6, 1
store i32 %7, i32* %tailPtr 
br label %loop.cond

loop.end:
%8 = add i32 2, 3
ret i32 %8 
}


