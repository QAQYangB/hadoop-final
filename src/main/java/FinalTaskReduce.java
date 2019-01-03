import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FinalTaskReduce extends Reducer<Text, IntWritable, Text, Text> {

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
//        if (key.toString().contains("-")) {
            long num = 0;

            //对map传过来的数据进行计数
            for (IntWritable value : values) {
                num++;
            }
//            System.out.println(key + "\t" + num);
            //计数结果写入文件
            context.write(key, new Text(num+""));
//        }
    }
}