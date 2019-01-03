import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class FinalTask {
    public static void main(String[] args) {
        //Windows下需要设置Hadoop
        System.setProperty("hadoop.home.dir", "C:\\Users\\67158\\Desktop\\winutils-master\\hadoop-2.8.3");

        //得到Configuration并设置相关属性
        Configuration configuration = new Configuration();
        configuration.set("fs.default.name", "hdfs://192.168.111.101:9000");
        configuration.set("fs.defaultFS", "hdfs://192.168.111.101:9000");
        //自定义文件名，配合CustomizeFileName使用
        configuration.set("mapreduce.output.basename", "2016081077_模型.txt");


        Job finalTaskJob = null;

        try {
            Path outPath = new Path("hdfs://192.168.111.101:9000/final_task/");
            FileSystem fileSystem = outPath.getFileSystem(configuration);
            if (fileSystem.exists(outPath)) {
                fileSystem.delete(outPath, true);
            }

            finalTaskJob = Job.getInstance(configuration);

            finalTaskJob.setJobName("finalTask");

            finalTaskJob.setJarByClass(FinalTask.class);
            finalTaskJob.setMapperClass(FinalTaskMapper.class);
            finalTaskJob.setReducerClass(FinalTaskReduce.class);

            finalTaskJob.setOutputKeyClass(Text.class);
            finalTaskJob.setOutputValueClass(Text.class);
            finalTaskJob.setMapOutputKeyClass(Text.class);
            finalTaskJob.setMapOutputValueClass(IntWritable.class);

            FileInputFormat.addInputPath(finalTaskJob, new Path("hdfs://192.168.111.101:9000/input_2016081077/"));
            FileOutputFormat.setOutputPath(finalTaskJob, new Path("hdfs://192.168.111.101:9000/final_task/"));

            finalTaskJob.setOutputFormatClass(CustomizeFileName.class);

            finalTaskJob.waitForCompletion(true);
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
