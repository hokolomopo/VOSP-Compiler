@.str = private unnamed_addr constant [22 x i8] c"Factorial of %d = %d\0A\00", align 1

define i32 @factorial(i32 %n) {
  %cmp = icmp sle i32 %n, 1
  br i1 %cmp, label %cond.true, label %cond.false

cond.true:                                        ; preds = %entry
  br label %cond.end

cond.false:                                       ; preds = %entry
  %sub = sub nsw i32 %n, 1
  %call = call i32 @factorial(i32 %sub)
  %mul = mul nsw i32 %n, %call
  br label %cond.end

cond.end:                                         ; preds = %cond.false, %cond.true
  %cond = phi i32 [ 1, %cond.true ], [ %mul, %cond.false ]
  ret i32 %cond
}

define i32 @main(i32 %argc, i8** %argv) {
  %retval = alloca i32, align 4
  %argc.addr = alloca i32, align 4
  %argv.addr = alloca i8**, align 8
  %void = alloca void, align 4   

  store i32 5, i32* %retval
  store i32 %argc, i32* %argc.addr, align 4
  store i8** %argv, i8*** %argv.addr, align 8
  %int = add i32 0, 10
  %call = call i32 @factorial(i32 %int)
  %call3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([22 x i8], [22 x i8]* @.str , i32 0, i32 0), i32 10, i32 %call)

  ret i32 %call
}

declare i32 @printf(i8*, ...)
