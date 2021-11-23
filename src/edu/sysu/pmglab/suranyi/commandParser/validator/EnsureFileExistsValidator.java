package edu.sysu.pmglab.suranyi.commandParser.validator;

import edu.sysu.pmglab.suranyi.commandParser.exception.ParameterException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author suranyi
 * @description 文件是否存在验证器
 */

public enum EnsureFileExistsValidator implements IValidator {
    /**
     * 单例
     */
    INSTANCE;

    @Override
    public void validate(String commandKey, Object params) {
        if (params instanceof String[]) {
            for (String fileName : (String[]) params) {
                if (!Files.exists(Paths.get(fileName))) {
                    throw new ParameterException("value " + "failed validate: no such file or directory (" + fileName + ")");
                }
            }
        } else if (params instanceof String) {
            if (!Files.exists(Paths.get((String) params))) {
                throw new ParameterException("value " + "failed validate: no such file or directory (" + params + ")");
            }
        } else if (params instanceof Map){
            for (String fileName : ((Map<?, String>) params).values()) {
                if (!Files.exists(Paths.get(fileName))) {
                    throw new ParameterException("value " + "failed validate: no such file or directory (" + fileName + ")");
                }
            }
        } else {
            throw new ParameterException("unable to infer the type of " + commandKey);
        }
    }

    @Override
    public String toString() {
        return "EnsureFileExists";
    }
}