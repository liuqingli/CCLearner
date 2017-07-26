package vt.cs;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CCLearner_Test {

    public static String config_file = "/home/cclearner/Desktop/CCLearner/CCLearner.conf";

    public static String source_file_path;
    public static String clone_file_path;

    public static String output_dir;

    public static String[] testing_folder;

    public static ArrayList<File> files = new ArrayList<>();
    public static MethodList methodVectorList = new MethodList();
    public static ASTParserTool ASTparserTool = new ASTParserTool();

    public static MethodSimilarity methodSim = new MethodSimilarity();

    public static String model_File;
    public static MultiLayerNetwork model;

    public static int feature_num;
    public static String feature_name;

    public static String sim_File;
    public static String pos_File;

    public static double[] sim;
    public static double pre_sim;
    public static double clone_Threshold;

    public static PrintWriter writer = null;

    public static void Load_Config(){
        try {
            Properties prop = new Properties();
            InputStream is = new FileInputStream(config_file);

            prop.load(is);

            output_dir = prop.getProperty("output.dir");

            source_file_path = prop.getProperty("source.file.path");
            clone_file_path = prop.getProperty("clones.file.path");

            testing_folder = prop.getProperty("testing.folder").split(",");

            feature_num = Integer.valueOf(prop.getProperty("feature.num"));
            feature_name = prop.getProperty("feature.name");

            model_File = prop.getProperty("model.file.path");
            pos_File = prop.getProperty("pos.file.path");
            sim_File = prop.getProperty("sim.file.path");

            clone_Threshold = Double.valueOf(prop.getProperty("testing.sim_threshold"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

        Load_Config();

        model = ModelSerializer.restoreMultiLayerNetwork(model_File);

        long start = System.nanoTime();

        for (int index = testing_folder.length - 1; index >= 0; index--) {

            writer = new PrintWriter(output_dir + testing_folder[index] + ".csv", "UTF-8");

            files.clear();

            System.out.println("------------------------------------------------------");
            System.out.println("Get files from directory " + testing_folder[index] + "...");
            GetAllFilesFromDirectory(source_file_path + "/" + testing_folder[index]);
            System.out.println("Finish!");

            methodVectorList.clear();

            System.out.println("------------------------------------------------------");
            System.out.println("Get methods from files...");
            GetAllMethodsFromFiles();
            System.out.println("# of methods: " + methodVectorList.size());
            System.out.println("Finish!");

            System.out.println("------------------------------------------------------");
            System.out.println("Calculating similarity and writing to file...");
            CreateSimFile();
            System.out.println("Finish!");

            System.out.println("------------------------------------------------------");
            System.out.println("Checking clone pairs...");
            ClonePairChecking();
            System.out.println("Finish!");

            writer.close();
        }

        mergeTestData();

        long end = System.nanoTime();
        System.out.println("Time Cost: " + TimeUnit.NANOSECONDS.toMillis(end - start) + "ms");
    }

    public static void GetAllFilesFromDirectory(String dir_name) {

        File directory = new File(dir_name);
        File[] file_List = directory.listFiles();
        for (File file : file_List) {
            if (file.isFile()) {
                files.add(file);
            }
            else if (file.isDirectory()) {
                GetAllFilesFromDirectory(file.getAbsolutePath());
            }
        }
    }

    public static void GetAllMethodsFromFiles() throws Exception {

        for(int index1 = 0; index1 < files.size(); index1++) {
            ASTparserTool.setFileName(files.get(index1).getAbsolutePath());
            methodVectorList = ASTparserTool.parseMethod(files.get(index1).getAbsolutePath());
        }
    }

    public static void CreateSimFile() throws Exception {

        int writer_flush_count = 0;
        PrintWriter writer1 = new PrintWriter(sim_File, "UTF-8");
        PrintWriter writer2 = new PrintWriter(pos_File, "UTF-8");

        for (int i = 0; i < methodVectorList.size() - 1; i++) {
            if(i % 500 == 0)
                System.out.println("Method processing: " + i + "/" + methodVectorList.size());
            for (int j = i + 1; j < methodVectorList.size(); j++) {

                int lines_i = methodVectorList.getMethodVector(i).endLineNumber - methodVectorList.getMethodVector(i).startLineNumber + 1;
                int lines_j = methodVectorList.getMethodVector(j).endLineNumber - methodVectorList.getMethodVector(j).startLineNumber + 1;

                if(Math.max(lines_i, lines_j) <= 3 * Math.min(lines_i, lines_j)) {

                    writer_flush_count++;

                    if(feature_num == 8) {
                        sim = methodSim.methodVectorSim(methodVectorList.getMethodVector(i), methodVectorList.getMethodVector(j));
                        pre_sim = sim[0] * 0.125 + sim[1] * 0.125 + sim[2] * 0.125 + sim[3] * 0.125 + sim[4] * 0.125 + sim[5] * 0.125 + sim[6] * 0.125 + sim[7] * 0.125;
                    }
                    else if(feature_num == 7) {
                        sim = methodSim.methodVectorSim(methodVectorList.getMethodVector(i), methodVectorList.getMethodVector(j), feature_name);
                        pre_sim = sim[0] * 0.14 + sim[1] * 0.14 + sim[2] * 0.14 + sim[3] * 0.14 + sim[4] * 0.14 + sim[5] * 0.15 + sim[6] * 0.15;
                    }

                    if(pre_sim >= 0.5) {

                        for (int index = 0; index < sim.length - 1; index++)
                            writer1.print(sim[index] + ",");
                        writer1.print(sim[sim.length - 1] + "\n");

                        String[] t1 = methodVectorList.getMethodVector(i).fileName.split("/");
                        String type1 = t1[t1.length - 2];
                        String file1 = t1[t1.length - 1];
                        String[] t2 = methodVectorList.getMethodVector(j).fileName.split("/");
                        String type2 = t2[t2.length - 2];
                        String file2 = t2[t2.length - 1];

                        writer2.write("1" + ","
                            + type1 + "," + file1 + "," + methodVectorList.getMethodVector(i).startLineNumber + "," + methodVectorList.getMethodVector(i).endLineNumber + ","
                            + type2 + "," + file2 + "," + methodVectorList.getMethodVector(j).startLineNumber + "," + methodVectorList.getMethodVector(j).endLineNumber + "\n");

                        if (writer_flush_count == 500) {
                            writer1.flush();
                            writer_flush_count = 0;
                        }
                    }
                }
            }
        }
        writer1.close();
        writer2.close();
    }

    public static void ClonePairChecking() throws Exception {

        Scanner scanner1 = new Scanner(new File(sim_File));
        scanner1.useDelimiter(",|\\n");

        Scanner scanner2 = new Scanner(new File(pos_File));
        scanner2.useDelimiter("\\n");

        double[] matrix = new double[feature_num];
        int column = 0;
        while (scanner1.hasNext()) {
            matrix[column] = Double.valueOf(scanner1.next());
            column++;
            if(column % feature_num == 0) {
                String result = scanner2.next();
                column = 0;
                INDArray sim_Matrix = Nd4j.create(matrix);
                INDArray predicted = model.output(sim_Matrix);

                if(predicted.getDouble(1) >= clone_Threshold) {
                    writer.write(result + "\n");
                }
            }
        }
    }


    public static void mergeTestData() throws Exception {

        String line;

        FileWriter writer = new FileWriter(clone_file_path);
        for (int i = 0; i < testing_folder.length; i++) {
            BufferedReader br = new BufferedReader(new FileReader(output_dir + testing_folder[i] + ".csv"));
            while ((line = br.readLine()) != null) {
                // writer.write("1," + line + "\n");
                writer.write(line + "\n");
                writer.flush();
            }
        }
        writer.close();

        try {
            for (int i = 0; i < testing_folder.length; i++) {
                File file = new File(output_dir + testing_folder[i] + ".csv");
                file.delete();
            }
            File pos_file = new File(pos_File);
            pos_file.delete();
            File sim_file = new File(sim_File);
            sim_file.delete();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
