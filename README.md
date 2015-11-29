# Comp560HW4

There are two src folders: tuning and testResults

tuning is our src code for getting the optimal parameters for each of the four run configurations. 
We used a nested loop to do so and changed the ranges based on what we want to tune for, and manipulated the two
booleans near the top of the program to determine run configuration.

testResults will test our SVMs against the test data and print out the results. our runnable jar will run this code.

There are also verbose logs for each of our tuning sessions and testing sessions, these have detailed information on every
classification made during each run including individual probabilities computed by each svm.

INSTRUCTIONS FOR RUNNING THE JAR:   Note:images are exported into jar, hence the large jar size
java -jar A4Runner.jar w x y z
where
w is a binary boolean, if 1 you wish to use a histogram representation, if 0 tinyImage representation
x is a binary boolean, if 1 you wish to use linear kernel, if 0 rbf kernel
y is a binary boolean for verbose txt file output along with the console output (much like the txt files provided) if 1 
you must enter file path in next argument, if 0 no output to txt file desired
z is the optional path to txt file you wish to create/write to, is required if y is true
