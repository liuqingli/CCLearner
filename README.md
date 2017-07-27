# CCLearner

## Folders and Files
- BigCloneBench -- Includes raw java files and sql script for labeled data
- Run -- Jar Files and dependencies for easy mode
- CCLearner_Feature -- Generate data for training model
- CCLearner_Train -- Generate training models
- CCLearner_Test -- Detect clone pairs by leveraging training models
- Recall_Query -- SQL scripts for calculating recall rates of different types of clones
- CCLearner.conf -- Configuration file of CCLearner

## Prerequisite
- Ubuntu14.04, JAVA 8

## BigCloneBench Preparation
#### Extract SQL script
```
$ tar -xvzf era_bigclonebench.sql.tar.gz
```
#### Extract raw java files
```
$ tar -xvzf era_bcb_sample.tar.gz
```
#### PostgreSQL installation
```
$ apt-get update
$ apt-get install postgresql postgresql-contrib
```
#### Database configuration and data import
```
# Change user
$ sudo -i -u postgres

# Run PostgreSQL console
$ psql

# Create dependent roles for BigCloneBench
postgres=# CREATE ROLE postgresql;
postgres=# CREATE ROLE bigclonebench;

# Data dump
postgres=# \i /home/cclearner/Desktop/CCLearner/era_bigclonebench.sql

# Create another user for use
CREATE USER cclearner with PASSWORD 'cclearner';
ALTER ROLE cclearner Superuser;
```
#### pgAdmin installation
```
$ apt-get install pgadmin3
```

## Customization
To run all the experiments in our paper, the following parameters could be changed. For 1-7, change the path with your own username and directory.
1. source.file.path
2. output.dir
3. feature.file.path
4. model.file.path
5. pos.file.path
6. sim.file.path
7. clones.file.path
8. feature.num  
9. feature.name
10. training.iteration
11. training.input.num
12. training.hidden.num (also need to modify the source file in CCLearner_Train)
13. testing.folder (users can reduce the number of testing folders to save time)

## Execution -- Easy Mode (Recommended)
By using the default or modified configuration file, go to Run folder and execute the following commands
```
java -jar CCLearner_Feature.jar
java -jar CCLearner_Train.jar
java -jar CCLearner_Test.jar (may take some time)
```

## Execution -- Developer Mode
To change datasets, more parameters or the source code, open CCLearner_Feature, CCLearner_Train, CCLearner_Test, rebuild and rerun the given project  

## Evaluation
#### Data import
Table "tools_clones" in PostgreSQL is used for data import. It is better to use pgAdmin to truncate table 
and import csv file into database.
1. Double click server's name to connect server and database
2. Right click "tools_clones" and click "truncate".
3. Right click "tools_clones" and click "import..." (Choose Filename; Format - "csv"; Encoding - "UTF8")

#### Calculate recall rate
In pgAdmin, click SQL icon on the top menu, choose one query file from Recall_Query folder and execute 
the query.

The numbers of true clones with different types in BigCloneBench for testing are T1(2,383), T2(671), VST3(873), ST3(5,365), MT3(31,413), WT3/4(1,540,513).

Recall Rate = Query Result / corresponding number of true clones
