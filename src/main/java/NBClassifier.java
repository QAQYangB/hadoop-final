import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class NBClassifier {
    // "..."应替换为所使用的数据的本地实际目录
    private static String trainingDataFilePath = "C:\\Users\\67158\\Desktop\\最终任务/training-10000000.txt";
    private static String modelFilePath = "C:\\Users\\67158\\Desktop\\Hadoop最终作业/2016081077_模型.txt";
    private static String testDataFilePath = "C:\\Users\\67158\\Desktop\\最终任务/test.txt";

    public static String[] extractFeatures(String sentence) {
        /*
         * 这里实现给定句子中包含的词语的提取，本实验中的句子已经分词，并且词语之间用空格隔开
         */

        return sentence.split(" ");

    }

    public static void train() {
        HashMap<String, Integer> parameters = new HashMap<String, Integer>();

        /*
         * 这里实现“类别-特征”对以及“类别”的计数，并以Key、Value的形式保存到Map中
         */
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(trainingDataFilePath)), 1024*1024*30);
            String line;

            /*
             * 避免在循环时频繁创建对象
             */
            String[] firstSplit;
            String[] secondSplit;
            String currentKey;
            Integer currentValue = 0;
            int praiseNum = 0;
            int badReviewNum = 0;

            while ((line = reader.readLine()) != null) {
                firstSplit = line.split("\t");
                if (firstSplit[0].equals("好评")) praiseNum++;
                else badReviewNum++;
                if (firstSplit.length != 1) {//本行字符串除了有好评差评的评价外，还有评论
                    secondSplit = firstSplit[1].split(" ");
                    for (String s : secondSplit) {
                        if (Pattern.matches(".*[^\\u4e00-\\u9fa5]+.*", s)) continue;//过滤含有非中文字符或者全为非中文字符的词语
                        currentKey = firstSplit[0] + "-" + s;
                        if (parameters.containsKey(currentKey)) {
                            parameters.put(currentKey, parameters.get(currentKey) + 1);
                        } else {
                            parameters.put(currentKey, 1);
                        }
                    }
                } else {//只有评价，没有评论，暂不处理
//                    currentKey = firstSplit[0];
//                    if (parameters.containsKey(currentKey)) {
//                        parameters.put(currentKey, parameters.get(currentKey) + 1);
//                    } else {
//                        parameters.put(currentKey, 1);
//                    }
                }
            }

            parameters.put("好评", praiseNum);
            parameters.put("差评", badReviewNum);

            reader.close();
            System.out.println(parameters.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveModel(parameters);
    }

    private static void saveModel(HashMap<String, Integer> parameters) {
        Iterator<String> keyIter = parameters.keySet().iterator();
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(modelFilePath));
            bw.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (keyIter.hasNext()) {
            String key = keyIter.next();
            int value = parameters.get(key);

            try {
                bw.append(key + "\t" + value + "\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HashMap<String, Integer> parameters = null;
    private static Set<String> V = null;
    private static double Nd;
    private static double sizeOfV;

    public static void loadModel() {
        V = new HashSet<String>();
        parameters = new HashMap<String, Integer>();

        try {
            List<String> parameterData = Files.readAllLines(Paths.get(modelFilePath));

            for (int i = 0; i < parameterData.size(); i++) {
                String parameter = parameterData.get(i);
                String key = parameter.substring(0, parameter.indexOf("\t"));
                Integer value = Integer.parseInt(parameter.substring(parameter.indexOf("\t") + 1));

                parameters.put(key, value);

                if (key.contains("-")) {
                    String feature = key.substring(key.indexOf("-") + 1);

                    V.add(feature);
                }

                if (!key.contains("-")) {
                    Nd += value;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String predict(String sentence) {
        String[] labels = {"好评", "差评"};
        String[] features = extractFeatures(sentence);

        double maxProb = Double.NEGATIVE_INFINITY;
        String prediction = null;

        /*
         * 这里实现预测模型
         */

        //好评和差评率均为0.5
        double goodReview = 0.5;
        double badReview = 0.5;

        int isSmoothing = 0;//用于判断是否需要平滑

        /*
            计算好评
         */
        for (String feature : features) {
            String key = labels[0]+"-"+feature;
            if (parameters.containsKey(key)) isSmoothing++;
        }
        if (isSmoothing != features.length) {   //需要平滑
            double denominator = 10000000+parameters.size();//分母
            for (String feature : features) {
                //利用正则表达式过滤含有非中文字符或者全为非中文字符的词语
                if (Pattern.matches(".*[^\\u4e00-\\u9fa5]+.*", feature)) continue;
                String key = labels[0]+"-"+feature;
                if (parameters.containsKey(key)) goodReview *= (parameters.get(key) + 1) / denominator;
                else goodReview *= 1 / (denominator+1);
            }
        } else {    //不需要平滑
            double denominator = 10000000;//分母
            //计算好评
            for (String feature : features) {
                //利用正则表达式过滤含有非中文字符或者全为非中文字符的词语
                if (Pattern.matches(".*[^\\u4e00-\\u9fa5]+.*", feature)) continue;
                String key = labels[0]+"-"+feature;
                goodReview *= parameters.get(key) / denominator;
            }
        }

        /*
          计算差评
         */
        isSmoothing = 0;
        for (String feature : features) {
            String key = labels[1]+"-"+feature;
            if (parameters.containsKey(key)) isSmoothing++;
        }
        if (isSmoothing != features.length) {   //需要平滑
            double denominator = 10000000+parameters.size();//分母
            for (String feature : features) {
                if (Pattern.matches(".*[^\\u4e00-\\u9fa5]+.*", feature)) continue;//过滤含有非中文字符或者全为非中文字符的词语
                String key = labels[1]+"-"+feature;
                if (parameters.containsKey(key)) badReview *= (parameters.get(key) + 1) / denominator;
                else badReview *= 1 / (denominator+1);
            }
        } else {    //不需要平滑
            double denominator = 10000000;//分母
            for (String feature : features) {
                if (Pattern.matches(".*[^\\u4e00-\\u9fa5]+.*", feature)) continue;//过滤含有非中文字符或者全为非中文字符的词语
                String key = labels[1]+"-"+feature;
                badReview *= parameters.get(key) / denominator;
            }
        }
        //根据计算结果给prediction赋值
        prediction = goodReview > badReview ? labels[0] : labels[1];

        return prediction;
    }

    public static void predictAll() {
        double accuracy = 0.;
        int amount = 0;

        File resultFile = new File(new File(modelFilePath).getParent()+"/2016081072_预测结果.txt");

        try {
            List<String> testData = Files.readAllLines(Paths.get(testDataFilePath));

            //输出结果文件
            BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile));

            for (String instance : testData) {
                String gold = instance.substring(0, instance.indexOf("\t"));
                String sentence = instance.substring(instance.indexOf("\t") + 1);
                String prediction = predict(sentence); // prediction为提交作业时需要按行输出到文件中的结果

                System.out.println("Gold='" + gold + "'\tPrediction='" + prediction + "'");

                if (gold.equals(prediction)) {
                    accuracy += 1.;
                }

                amount += 1;
                //结果写入文件
                writer.write(prediction+"\r\n");
            }
            //flush 缓冲并关闭writer
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Accuracy = " + accuracy / amount);
    }
}
