<template>
    <!-- 根容器：添加 overflow-hidden 防止边缘滚动条溢出 -->
    <div ref="editorContainer" class="monaco-container overflow-hidden"></div>
</template>

<script setup lang="ts">
    import { ref, onMounted, onBeforeUnmount, shallowRef, watch, nextTick } from 'vue';
    import * as monaco from 'monaco-editor';

    interface Props {
        modelValue: string;
        language?: string;
        theme?: string;
        // 允许父组件传入更精细的配置
        options?: monaco.editor.IStandaloneEditorConstructionOptions;
    }

    const props = withDefaults(defineProps<Props>(), {
        language: 'java', // 既然你主要用Java，这里默认值改一下
        theme: 'vs-dark',
        options: () => ({})
    });

    const emit = defineEmits(['update:modelValue', 'change', 'editor-mounted']);

    const editorContainer = ref<HTMLElement | null>(null);
    // shallowRef 极其重要：防止 Vue 深度追踪 Monaco 内部极其复杂的属性，避免内存溢出和性能卡顿
    const editor = shallowRef<monaco.editor.IStandaloneCodeEditor | null>(null);

    /**
     * 核心配置项：合并默认值与父组件传入的 options
     */
    const defaultOptions: monaco.editor.IStandaloneEditorConstructionOptions = {
        fontSize: 14,                // 字体大小
        fontFamily: 'Menlo, Monaco, "Courier New", monospace', // 常用等宽字体
        automaticLayout: true,       // 随父容器大小变化自动重绘（配合 Resizable 必开）
        minimap: { enabled: true },  // 开启右侧缩略图
        scrollBeyondLastLine: false, // 滚动完最后一行后停止，不留白
        readOnly: false,             // 是否只读
        renderWhitespace: 'none',    // 是否渲染空格/制表符
        tabSize: 4,                  // Java 习惯缩进 4格
        cursorStyle: 'line',         // 光标样式
    };

    onMounted(() => {
        if (editorContainer.value) {
            // 1. 初始化编辑器实例
            editor.value = monaco.editor.create(editorContainer.value, {
                value: props.modelValue,
                language: props.language,
                theme: props.theme,
                ...defaultOptions,
                ...props.options // 父组件配置具有最高优先级
            });

            // 2. 监听内容变化 (防抖逻辑可以加在外部，这里保持同步)
            editor.value.onDidChangeModelContent(() => {
                const value = editor.value?.getValue() || '';
                // 触发 v-model 同步
                emit('update:modelValue', value);
                emit('change', value);
            });

            // 3. 将实例通过事件暴露给父组件，方便外部进行复杂操作
            emit('editor-mounted', editor.value);
        }
    });

    /**
     * 监听 modelValue 变化
     * 场景：后端接口加载了代码，需要更新编辑器显示
     */
    watch(() => props.modelValue, (newValue) => {
        if (editor.value) {
            const currentValue = editor.value.getValue();
            if (newValue !== currentValue) {
                // 获取当前光标位置，防止 setValue 后光标跳回开头
                const position = editor.value.getPosition();
                editor.value.setValue(newValue);
                if (position) editor.value.setPosition(position);
            }
        }
    });

    /**
     * 监听语言变化
     */
    watch(() => props.language, (newLang) => {
        if (editor.value) {
            const model = editor.value.getModel();
            if (model) {
                monaco.editor.setModelLanguage(model, newLang);
            }
        }
    });

    /**
     * 监听主题变化
     */
    watch(() => props.theme, (newTheme) => {
        if (editor.value) {
            monaco.editor.setTheme(newTheme);
        }
    });

    /**
     * 监听配置项变化
     * 场景：动态切换只读模式、字体大小等
     */
    watch(() => props.options, (newOptions) => {
        if (editor.value) {
            editor.value.updateOptions({ ...newOptions });
        }
    }, { deep: true });

    /**
     * 辅助方法：触发自动格式化
     */
    const formatCode = () => {
        nextTick(() => {
            editor.value?.getAction('editor.action.formatDocument')?.run();
        });
    };

    // 组件销毁前必须释放资源，否则会导致严重的内存泄漏
    onBeforeUnmount(() => {
        if (editor.value) {
            editor.value.dispose();
        }
    });

    // 暴露 API
    defineExpose({
        getEditor: () => editor.value,
        formatCode
    });
</script>

<style scoped>
    .monaco-container {
        width: 100%;
        height: 100%;
        border: 1px solid #ccc;
        text-align: left;
    }
</style>