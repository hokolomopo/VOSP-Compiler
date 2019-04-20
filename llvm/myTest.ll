@.str2 = private unnamed_addr constant [22 x i8] c"Factorial of %d = %d\0A\00", align 1
@.str3 = private unnamed_addr constant [22 x i8] c"Main returne %d = %d\0A\00", align 1

@.str = private unnamed_addr constant [38 x i8] c"Viva la vida baby and keep on Rockin'\00"

%class.Main = type { i32, i32 }

define i32 @factorial(i32 %tail) {
  %tailPtr = alloca i32 
store i32 %tail, i32* %tailPtr 

%1 = load i32, i32* %tailPtr 
br label %loop.cond

loop.cond:
%2 = load i32, i32* %tailPtr 
%3 = icmp ult i32 %2, 5
br i1 %3, label %loop.start, label %loop.end

loop.start:
%4 = load i32, i32* %tailPtr 
%5 = add i32 %4, 1
store i32 %5, i32* %tailPtr 
br label %loop.cond

loop.end:
%6 = load i32, i32* %tailPtr 
ret i32 %6 
}

define i32 @main () { 
  %MainPtr = alloca %class.Main 
  %returned = call i32 @Main.main(%class.Main* %MainPtr)
  %call3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([22 x i8], [22 x i8]* @.str3 , i32 0, i32 0), i32 0, i32 %returned) 
  ret i32 %returned
}

define i32 @Main.main(%class.Main* %self) {
%selfPtr = alloca %class.Main* 
store %class.Main* %self, %class.Main** %selfPtr 
%xsPtr = alloca i32 
store i32 879, i32* %xsPtr 
%1 = load i32, i32* %xsPtr 
%2 = mul i32 %1, 2
%3 = getelementptr %class.Main, %class.Main* %self, i32 0, i32 0 
store i32 %2, i32* %3 
%4 = getelementptr %class.Main, %class.Main* %self, i32 0, i32 0 
%5 = load i32, i32* %4 
ret i32 %5 
}

define i32 @Main.factMain(%class.Main* %self) {
  %selfPtr = alloca %class.Main* 
  store %class.Main* %self, %class.Main** %selfPtr 

  %int = add i32 0, 3
  %call = call i32 @factorial(i32 %int)

  %mainPtr = alloca %class.Main

  %call3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([22 x i8], [22 x i8]* @.str2 , i32 0, i32 0), i32 10, i32 %call)

  ret i32 %call
}

declare i32 @printf(i8*, ...)
