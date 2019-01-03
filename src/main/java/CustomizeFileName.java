import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class CustomizeFileName extends TextOutputFormat {
    @Override
    public Path getDefaultWorkFile(TaskAttemptContext context, String extension) throws IOException {
        FileOutputCommitter fileOutputCommitter = (FileOutputCommitter) getOutputCommitter(context);
        return new Path(fileOutputCommitter.getWorkPath(), getOutputName(context));
    }
}
