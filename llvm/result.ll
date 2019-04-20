@.str = private unnamed_addr constant [38 x i8] c"Viva la vida baby and keep on Rockin'\00"
@.str.1 = private unnamed_addr constant [4 x i8] c"lol\00"


define i32 @main () { 
%MainPtr = alloca %class.Main 
%returned = call i32 @Main.main(%class.Main* %MainPtr)
ret i32 %returned
}


%class.Main = type { i32, i32 }

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
%1 = load i32, i32* %tailPtr 
%2 = add i32 %1, 3
store i32 %2, i32* %tailPtr 
%3 = load i32, i32* %tailPtr 
store i32 %3, i32* %tailPtr 
ret i32 %3 
}


