import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.regex.Pattern;

public class FinalTaskMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //以 \t 为分隔符进行分割
        String[] firstSplit = value.toString().split("\t");


        if (firstSplit.length == 1) {   //如果长度为 1 则只有评价没有评论
            //写入 情感 次数加一
            context.write(new Text(firstSplit[0]), new IntWritable(1));
            return;
        }

        String[] secondSplit = firstSplit[1].split(" ");
        for (String s : secondSplit) {
            //利用正则表达式过滤含有非中文字符或者全为非中文字符的词语
            if (Pattern.matches(".*[^\\u4e00-\\u9fa5]+.*", s)) continue;
            //写入 情感-词语
            context.write(new Text(firstSplit[0] + "-" + s), new IntWritable(1));
        }
    }

//    public static void main(String[] args) {
//        System.out.println(Pattern.matches("[^\\x00-\\xff]+<>L+([^\\x00-\\xff])+<>F+", "愤怒<>L超<>F"));
//    }
}
