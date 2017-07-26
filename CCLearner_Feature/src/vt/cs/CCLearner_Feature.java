package vt.cs;

import java.io.*;
import java.sql.*;
import java.util.Random;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class CCLearner_Feature {

    public static String config_file = "/home/cclearner/Desktop/CCLearner/CCLearner.conf";

	public static String connURL;
	public static String userName;
	public static String password;

	public static Connection conn;
	public static Statement stat;
	public static ResultSet res;
	public static ResultSetMetaData rsmd;

    public static String[] sql_type = {
        "and syntactic_type = 1 ",
        "and syntactic_type = 2 ",
        "and syntactic_type = 3 and similarity_line >= 0.9 ",
        "and syntactic_type = 3 and similarity_line < 0.9 and similarity_line >= 0.7 "
    };

    public static String source_file_path;
    public static String feature_file_path;

    public static String output_dir;

    public static int feature_num;
    public static String feature_name;
    public static int feature_minline;

    public static int[] True_Negative_Threshold = {13750, 3104, 1207, 4602};
    public static String[] True_Negative_Type = {"T1", "T2", "VST3", "ST3"};
    public static int True_Negative_Count;

    public static String train_CloneFile1, train_CloneFile2;
	public static int train_startline1, train_endline1;
	public static int train_startline2, train_endline2;

	public static ASTParserTool parserTool1 = new ASTParserTool();
	public static ASTParserTool parserTool2 = new ASTParserTool();
	public static MethodList methodVectorList1 = new MethodList();
	public static MethodList methodVectorList2 = new MethodList();

	public static PrintWriter true_writer;
    public static PrintWriter false_writer;

    public static void Load_Config(){
        try {
            Properties prop = new Properties();
            InputStream is = new FileInputStream(config_file);

            prop.load(is);

            connURL = prop.getProperty("postgreSQL.conn");
            userName = prop.getProperty("postgreSQL.user");
            password = prop.getProperty("postgreSQL.passwd");

            output_dir = prop.getProperty("output.dir");

            source_file_path = prop.getProperty("source.file.path");
            feature_file_path = prop.getProperty("feature.file.path");

            feature_num = Integer.valueOf(prop.getProperty("feature.num"));
            feature_name = prop.getProperty("feature.name");
            feature_minline = Integer.valueOf(prop.getProperty("feature.minline"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

		// TODO Auto-generated method stub

        Load_Config();

		System.out.println("START!!");

		long start = System.nanoTime();

        for (int i = 0; i < 4; i++) {

            True_Negative_Count = 0;

            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(connURL, userName, password);

            String sql = "select A.type as type1, A.name as name1, A.startline as startline1, A.endline as endline1, "
                + "B.type as type2, B.name as name2, B.startline as startline2, B.endline as endline2, M.functionality_id as func_id "
                + "from "
                + "( "
                + "select * from clones "
                + "where functionality_id = 4 "
                + sql_type[i]
                + ") M "
                + "INNER JOIN functions A on M.function_id_one = A.id "
                + "INNER JOIN functions B on M.function_id_two = B.id ";

            conn.setAutoCommit(false);
            stat = conn.createStatement();
            stat.setFetchSize(500);
            res = stat.executeQuery(sql);
            rsmd = res.getMetaData();

            true_writer = new PrintWriter(output_dir + String.valueOf(i + 1) + ".csv", "UTF-8");
            false_writer = new PrintWriter(output_dir + "N" + String.valueOf(i + 1) + ".csv", "UTF-8");

            int count = 0;

            while (res.next()) {
                String[] row = new String[rsmd.getColumnCount()];
                for (int index = 0; index < rsmd.getColumnCount(); index++)
                    row[index] = String.valueOf(res.getObject(index + 1));

                methodVectorList1.clear();
                methodVectorList2.clear();

                train_CloneFile1 = source_file_path + row[8] + "/" + row[0] + "/" + row[1];
                train_startline1 = Integer.valueOf(row[2]);
                train_endline1 = Integer.valueOf(row[3]);

                train_CloneFile2 = source_file_path + row[8] + "/" + row[4] + "/" + row[5];
                train_startline2 = Integer.valueOf(row[6]);
                train_endline2 = Integer.valueOf(row[7]);

                if (Integer.valueOf(row[3]) - Integer.valueOf(row[2]) + 1 >= feature_minline &&
                    Integer.valueOf(row[7]) - Integer.valueOf(row[6]) + 1 >= feature_minline) {

                    methodVectorList1 = parserTool1.parseMethod(train_CloneFile1);
                    methodVectorList2 = parserTool2.parseMethod(train_CloneFile2);

                    Generate_TrueClones();
                    Generate_FalseClones(i);

                    count++;
                }
            }

            System.out.println("Extracting True/False Clone Pairs -"  + True_Negative_Type[i] + "\t: " + count + "/" + count);

            if (res != null)
                res.close();
            if (stat != null)
                stat.close();
            if (conn != null)
                conn.close();

            true_writer.close();
            false_writer.close();
        }

        MergeFiles();

        System.out.println("COMPLETE!!");

        long end = System.nanoTime();

        System.out.println("Time Cost:\t" + TimeUnit.NANOSECONDS.toMillis(end - start) + "ms");
    }

	public static void Generate_TrueClones() {

		// Locate each method in source files
		int rec_i = 0, rec_j = 0;

		int dis1 = 999999, dis2 = 999999;

		for(int i = 0; i < methodVectorList1.size(); i++) {
			int dis = Math.abs(methodVectorList1.getMethodVector(i).startLineNumber - train_startline1) +
					Math.abs(methodVectorList1.getMethodVector(i).endLineNumber - train_endline1);
			if(dis < dis1) {
				dis1 = dis;
				rec_i = i;
			}
		}
		for(int j = 0; j < methodVectorList2.size(); j++) {
			int dis = Math.abs(methodVectorList2.getMethodVector(j).startLineNumber - train_startline2) +
					Math.abs(methodVectorList2.getMethodVector(j).endLineNumber - train_endline2);
			if(dis < dis2) {
				dis2 = dis;
				rec_j = j;
			}
		}

		// Output similarity vector of true clones
		MethodSimilarity methodSim = new MethodSimilarity();

		if(feature_num == 8) {
            double[] sim = methodSim.methodVectorSim(methodVectorList1.getMethodVector(rec_i), methodVectorList2.getMethodVector(rec_j));
            Write_TrueClones(sim);
        }
		else if (feature_num == 7) {
            double[] sim = methodSim.methodVectorSim(methodVectorList1.getMethodVector(rec_i), methodVectorList2.getMethodVector(rec_j), feature_name);
            Write_TrueClones(sim);
        }
	}

	public static void Write_TrueClones(double[] sim) {
        true_writer.print("1");
        for(int index = 0; index < sim.length; index++)
            true_writer.print("," + sim[index]);
        true_writer.println();
        true_writer.flush();
    }

    public static void Generate_FalseClones(int pos) {

        // Locate each method in source files
        int rec_i = 0, rec_j = 0;

        int dis1 = 999999, dis2 = 999999;

        for(int i = 0; i < methodVectorList1.size(); i++) {
            int dis = Math.abs(methodVectorList1.getMethodVector(i).startLineNumber - train_startline1) +
                Math.abs(methodVectorList1.getMethodVector(i).endLineNumber - train_endline1);
            if(dis < dis1) {
                dis1 = dis;
                rec_i = i;
            }
        }
        for(int j = 0; j < methodVectorList2.size(); j++) {
            int dis = Math.abs(methodVectorList2.getMethodVector(j).startLineNumber - train_startline2) +
                Math.abs(methodVectorList2.getMethodVector(j).endLineNumber - train_endline2);
            if(dis < dis2) {
                dis2 = dis;
                rec_j = j;
            }
        }

        for(int i = 0; i < methodVectorList1.size(); i++) {
            for(int j = 0; j < methodVectorList2.size(); j++){
                if(i != rec_i && j != rec_j) {

                    MethodSimilarity methodSim = new MethodSimilarity();

                    if(feature_num == 8) {
                        double[] sim = methodSim.methodVectorSim(methodVectorList1.getMethodVector(i), methodVectorList2.getMethodVector(j));
                        double methodSimilarity = sim[0] * 0.125 + sim[1] * 0.125 + sim[2] * 0.125 + sim[3] * 0.125 + sim[4] * 0.125 + sim[5] * 0.125 + sim[6] * 0.125 + sim[7] * 0.125;
                        Random rand = new Random();
                        int r_number = rand.nextInt(10) + 1;
                        if (True_Negative_Count < True_Negative_Threshold[pos] && methodSimilarity <= 0.2 && r_number <= 5) {
                            false_writer.print("0");
                            for (int index = 0; index < sim.length; index++)
                                false_writer.print("," + sim[index]);
                            false_writer.println();
                            false_writer.flush();

                            True_Negative_Count++;
                        }
                    }
                    else if(feature_num == 7) {
                        double[] sim = methodSim.methodVectorSim(methodVectorList1.getMethodVector(i), methodVectorList2.getMethodVector(j), feature_name);
                        double methodSimilarity = sim[0] * 0.14 + sim[1] * 0.14 + sim[2] * 0.14 + sim[3] * 0.14 + sim[4] * 0.14 + sim[5] * 0.15 + sim[6] * 0.15;
                        Random rand = new Random();
                        int r_number = rand.nextInt(10) + 1;
                        if(True_Negative_Count < True_Negative_Threshold[pos] && methodSimilarity <= 0.2 && r_number <= 5) {
                            false_writer.print("0");
                            for(int index = 0; index < sim.length; index++)
                                false_writer.print("," + sim[index]);
                            false_writer.println();
                            false_writer.flush();

                            True_Negative_Count++;
                        }
                    }
                }
            }
        }
    }

    public static void MergeFiles() throws Exception {

        String line;

        FileWriter writer = new FileWriter(feature_file_path);

        System.out.println("Merging Training Files...");

        for(int i = 0; i < 4 ; i++) {
            BufferedReader br = new BufferedReader(new FileReader(output_dir + String.valueOf(i+1) + ".csv"));
            while ((line = br.readLine()) != null) {
                writer.write(line + "\n");
                writer.flush();
            }
            br = new BufferedReader(new FileReader(output_dir + "N" + String.valueOf(i+1) + ".csv"));
            while ((line = br.readLine()) != null) {
                writer.write(line + "\n");
                writer.flush();
            }
        }
        writer.close();

        System.out.println("Deleting Old Files...");

        try {
            for (int i = 0; i < 4; i++) {
                File file = new File(output_dir + String.valueOf(i+1) + ".csv");
                file.delete();
                file = new File(output_dir + "N" + String.valueOf(i+1) + ".csv");
                file.delete();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
