package com.zbn.springbootinit.service;

import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionContentPart;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.zbn.springbootinit.config.ArkClientConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageToTextService {

    @Resource
    ArkClientConfig arkConfig;

    public String getText(List<String> imageUrl) {

        final List<ChatMessage> messages = new ArrayList<>();
        // 添加系统角色的消息
        messages.add(ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(
                """                    
                                你是一位正在通过语音授课的教师，需要根据幻灯片生成可直接朗读的教学脚本。请遵守：
                                1. 纯口语化表达，使用"同学们注意"、"我们来看"等课堂用语
                                2. 完全以第一人称讲解（如"这张幻灯片展示的是..."）
                                3. 禁止出现"本节将介绍"、"如图所示"等视觉描述
                                4. 每页讲解包含：
                                   - 知识要点直述
                                   - 生活化举例
                                5. 自然衔接页间内容（如"理解了这一点后，我们继续看下一页..."）
                                6. 使用初中生易懂的比喻和口语化表达
                                7. 每段控制在150字左右
                        """
        ).build());
        final List<ChatCompletionContentPart> multiParts = new ArrayList<>();
        ArrayList<String> resultText = new ArrayList<>();
        multiParts.add(ChatCompletionContentPart.builder().type("text").text(
                """
                        请按顺序解析以下教学幻灯片（共%d张），遵守：
                        1. 严格按PPT顺序生成讲解
                        2. 对复杂图表需分步骤解读
                        3. 若检测到公式，先用通俗语言解释符号含义
                        4. 每页生成后立即添加---END PAGE---分隔符
                        5. 避免使用专业术语缩写
                        """
        ).build());
        for (String url : imageUrl) {
            multiParts.add(ChatCompletionContentPart.builder().type("image_url").imageUrl(
                    new ChatCompletionContentPart.ChatCompletionContentPartImageURL(
                            url
                    )
            ).build());
        }
        final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER)
                .multiContent(multiParts).build();
        messages.add(userMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("ep-20250207094637-7jsbf")
                .messages(messages)
                .build();
        arkConfig.arkService().createChatCompletion(chatCompletionRequest).getChoices().forEach(choice
                -> resultText.add(choice.getMessage().getContent().toString().replaceAll("\\r\\n|\\r|\\n", "")));
        arkConfig.arkService().shutdownExecutor();
        return resultText.get(0);
    }

    public String getImageText(List<String> imageUrl) {
        final List<ChatMessage> messages = new ArrayList<>();
        // 添加系统角色的消息
        messages.add(ChatMessage.builder()
                .role(ChatMessageRole.SYSTEM)
                .content("""
                        你是一个专业的书籍OCR识别引擎，需要严格遵守以下规则：
                        1. 只提取图片中的印刷体文字内容
                        2. 保持原始文本的段落结构和标点符号
                        3. 遇到模糊文字时用[?]标记不确定字符
                        4. 数学公式保持Latex格式
                        5. 完全忽略图片中的装饰性元素、手写注释和水印
                        6. 使用与原始文本相同的语言输出
                        7. 不添加任何解释性文字
                        """)
                .build());
        final List<ChatCompletionContentPart> multiParts = new ArrayList<>();
        ArrayList<String> resultText = new ArrayList<>();
        multiParts.add(ChatCompletionContentPart.builder()
                .type("text")
                .text("""
                        请识别以下书籍页面中的所有印刷体文字：
                                
                        要求：
                        1. 按原始顺序输出文字内容
                        2. 保留段落分隔（使用空行）
                        3. 遇到以下情况时特殊处理：
                           - 分栏排版保持栏位顺序
                           - 插图说明文字用【图注：...】标注
                           - 页码自动过滤
                           - 脚注保留原始位置
                        4. 多语言混合时保持原始语言
                        5. 字体大小变化不影响文本结构
                        6. 表格内容转换为Markdown格式
                        7. 数学公式保持Latex表示
                        8. 遇到无法识别的文字块使用[模糊文本]占位
                        """)
                .build());
        for (String url : imageUrl) {
            multiParts.add(ChatCompletionContentPart.builder().type("image_url").imageUrl(
                    new ChatCompletionContentPart.ChatCompletionContentPartImageURL(
                            url
                    )
            ).build());
        }
        final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER)
                .multiContent(multiParts).build();
        messages.add(userMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("ep-20250207094637-7jsbf")
                .messages(messages)
                .build();
        arkConfig.arkService().createChatCompletion(chatCompletionRequest).getChoices().forEach(choice
                -> resultText.add(choice.getMessage().getContent().toString()));
        arkConfig.arkService().shutdownExecutor();
        return resultText.get(0);
    }
}
