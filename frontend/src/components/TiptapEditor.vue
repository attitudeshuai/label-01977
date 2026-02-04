<script setup>
import { useEditor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Placeholder from '@tiptap/extension-placeholder'
import Underline from '@tiptap/extension-underline'
import { watch, onBeforeUnmount } from 'vue'
import { 
  Bold, Italic, Underline as UnderlineIcon, Strikethrough,
  Heading1, Heading2, Heading3,
  List, ListOrdered, Quote, Code,
  Undo, Redo
} from 'lucide-vue-next'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '开始记录今天的故事...'
  }
})

const emit = defineEmits(['update:modelValue'])

const editor = useEditor({
  content: props.modelValue,
  extensions: [
    StarterKit.configure({
      heading: {
        levels: [1, 2, 3]
      }
    }),
    Underline,
    Placeholder.configure({
      placeholder: props.placeholder
    })
  ],
  editorProps: {
    attributes: {
      class: 'prose prose-lg max-w-none focus:outline-none min-h-[200px] px-4 py-3'
    }
  },
  onUpdate: ({ editor }) => {
    emit('update:modelValue', editor.getHTML())
  }
})

// Watch for external changes
watch(() => props.modelValue, (value) => {
  if (editor.value && value !== editor.value.getHTML()) {
    editor.value.commands.setContent(value, false)
  }
})

onBeforeUnmount(() => {
  editor.value?.destroy()
})

// Toolbar button config
const toolbarButtons = [
  { action: () => editor.value?.chain().focus().toggleBold().run(), icon: Bold, active: () => editor.value?.isActive('bold'), title: '加粗' },
  { action: () => editor.value?.chain().focus().toggleItalic().run(), icon: Italic, active: () => editor.value?.isActive('italic'), title: '斜体' },
  { action: () => editor.value?.chain().focus().toggleUnderline().run(), icon: UnderlineIcon, active: () => editor.value?.isActive('underline'), title: '下划线' },
  { action: () => editor.value?.chain().focus().toggleStrike().run(), icon: Strikethrough, active: () => editor.value?.isActive('strike'), title: '删除线' },
  { type: 'divider' },
  { action: () => editor.value?.chain().focus().toggleHeading({ level: 1 }).run(), icon: Heading1, active: () => editor.value?.isActive('heading', { level: 1 }), title: '标题1' },
  { action: () => editor.value?.chain().focus().toggleHeading({ level: 2 }).run(), icon: Heading2, active: () => editor.value?.isActive('heading', { level: 2 }), title: '标题2' },
  { action: () => editor.value?.chain().focus().toggleHeading({ level: 3 }).run(), icon: Heading3, active: () => editor.value?.isActive('heading', { level: 3 }), title: '标题3' },
  { type: 'divider' },
  { action: () => editor.value?.chain().focus().toggleBulletList().run(), icon: List, active: () => editor.value?.isActive('bulletList'), title: '无序列表' },
  { action: () => editor.value?.chain().focus().toggleOrderedList().run(), icon: ListOrdered, active: () => editor.value?.isActive('orderedList'), title: '有序列表' },
  { action: () => editor.value?.chain().focus().toggleBlockquote().run(), icon: Quote, active: () => editor.value?.isActive('blockquote'), title: '引用' },
  { action: () => editor.value?.chain().focus().toggleCodeBlock().run(), icon: Code, active: () => editor.value?.isActive('codeBlock'), title: '代码块' },
  { type: 'divider' },
  { action: () => editor.value?.chain().focus().undo().run(), icon: Undo, title: '撤销' },
  { action: () => editor.value?.chain().focus().redo().run(), icon: Redo, title: '重做' },
]
</script>

<template>
  <div class="border-2 border-gray-200 rounded-md overflow-hidden focus-within:border-peach focus-within:ring-2 focus-within:ring-peach/20 transition-all">
    <!-- Toolbar -->
    <div class="flex flex-wrap items-center gap-1 p-2 bg-gray-50 border-b border-gray-200">
      <template v-for="(btn, index) in toolbarButtons" :key="index">
        <div v-if="btn.type === 'divider'" class="w-px h-5 bg-gray-300 mx-1"></div>
        <button
          v-else
          type="button"
          @click="btn.action"
          :title="btn.title"
          class="p-2 rounded hover:bg-gray-200 transition-colors"
          :class="btn.active?.() ? 'bg-peach-50 text-peach' : 'text-gray-600'"
        >
          <component :is="btn.icon" class="w-4 h-4" />
        </button>
      </template>
    </div>

    <!-- Editor -->
    <EditorContent :editor="editor" class="bg-white" />
  </div>
</template>

<style>
/* Tiptap Editor Styles */
.ProseMirror {
  min-height: 200px;
}

.ProseMirror p.is-editor-empty:first-child::before {
  content: attr(data-placeholder);
  float: left;
  color: #adb5bd;
  pointer-events: none;
  height: 0;
}

.ProseMirror h1 {
  font-size: 1.75rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.ProseMirror h2 {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.ProseMirror h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.ProseMirror blockquote {
  border-left: 4px solid #FF9A8B;
  padding-left: 1rem;
  margin: 1rem 0;
  background: #FFF5F3;
  padding: 0.5rem 1rem;
  border-radius: 0 8px 8px 0;
}

.ProseMirror pre {
  background: #f4f4f5;
  padding: 1rem;
  border-radius: 8px;
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.875rem;
}

.ProseMirror ul {
  list-style-type: disc;
  padding-left: 1.5rem;
}

.ProseMirror ol {
  list-style-type: decimal;
  padding-left: 1.5rem;
}

.ProseMirror li {
  margin: 0.25rem 0;
}
</style>
