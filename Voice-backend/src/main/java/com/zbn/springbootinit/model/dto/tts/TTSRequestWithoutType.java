package com.zbn.springbootinit.model.dto.tts;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 语音合成请求
 */
@NoArgsConstructor
@Data
public class TTSRequestWithoutType {
    /**
     * 合成文本
     */
    private String text;
    private String text_lang; // 文本语言
    private String ref_audio_path; // 参考音频路径
    private String prompt_text; // 提示文本
    private String prompt_lang; // 提示文本的语言
    private int top_k; // 采样时考虑的最可能词汇数量
    private float top_p; // 核采样的累积概率阈值
    private float temperature; // 控制输出随机性的温度参数
    private String text_split_method; // 文本分割方法
    private int batch_size; // 推理批次大小
    private float batch_threshold; // 批次分割阈值
    private boolean split_bucket; // 是否将批次分成多个桶
    private float speed_factor; // 合成音频的速度控制因子
    private boolean streaming_mode; // 是否返回流式响应
    private int seed; // 随机种子，用于结果重现
    private boolean parallel_infer; // 是否使用并行推理
    private float repetition_penalty; // T2S模型的重复惩罚

    public TTSRequestWithoutType(String text, String text_lang) {
        this.text = text;
        this.text_lang = text_lang;
        this.ref_audio_path = "";
        this.prompt_text = "";
        this.prompt_lang = "zh";
        this.top_k = 5;
        this.top_p = 1;
        this.temperature = 1;
        this.text_split_method = "cut1";
        this.batch_size = 40;
        this.batch_threshold = 0.75f;
        this.split_bucket = true;
        this.speed_factor = 1.0f;
        this.streaming_mode = false;
        this.seed = -1;
        this.parallel_infer = true;
        this.repetition_penalty = 1.35f;
    }

    public TTSRequestWithoutType(String text, String text_lang, String ref_audio_path) {
        this.text = text;
        this.text_lang = text_lang;
        this.ref_audio_path = "";
        this.prompt_text = "";
        this.prompt_lang = "zh";
        this.top_k = 5;
        this.top_p = 1;
        this.temperature = 1;
        this.text_split_method = "cut1";
        this.batch_size = 50;
        this.batch_threshold = 0.75f;
        this.split_bucket = true;
        this.speed_factor = 1.0f;
        this.streaming_mode = false;
        this.seed = -1;
        this.parallel_infer = true;
        this.repetition_penalty = 1.35f;
    }
}
