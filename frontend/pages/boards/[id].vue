<template>
  <div class="container">
    <!-- 상단 네비게이션 바 -->
    <div class="nav-bar">
      <NuxtLink to="/" class="btn-back">← 전체 목록</NuxtLink>
    </div>

    <div v-if="pending" class="status-box">데이터를 불러오는 중입니다...</div>
    <div v-else-if="error" class="status-box error">
      데이터를 불러올 수 없습니다: {{ error.message }}
    </div>

    <!-- 상세 / 수정 문서 카드 -->
    <article v-else-if="board" class="doc-card">
      <header class="doc-header">
        <span class="badge">문서 #{{ board.id }}</span>
        
        <!-- [조회 모드] 제목 -->
        <h1 v-if="!isEditing" class="doc-title">
          <pre style="background: #f1f5f9; padding: 10px; border-radius: 4px;">{{ board }}</pre>
          {{ board.title }}
          <span v-if="board.updatedAt && board.updatedAt !== board.createdAt"
          class="badge-edited">(수정됨)</span>
        </h1>
        <!-- [수정 모드] 제목 입력창 -->
        <div v-else class="edit-input-group">
          <label>제목</label>
          <input v-model="editForm.title" type="text" class="edit-input" required />
        </div>
      </header>

      <!-- 메타정보 요약표 -->
      <table class="meta-table">
        <tbody>
          <tr>
            <th>작성자</th>
            <td>
              <span class="author-name">{{ board.nickname }}</span>
              <span class="author-ip font-mono">{{ maskIp(board.ipAddress) }}</span>
            </td>
            <th>작성일시</th>
            <td class="font-mono">{{ board.createdAt || '-' }}</td>
          </tr>
          <!-- [수정 모드일 때만 표시되는 비밀번호 입력칸] -->
          <tr v-if="isEditing">
            <th>비밀번호</th>
            <td colspan="3">
              <input
                v-model="editForm.password"
                type="password"
                class="edit-input pw-input"
                placeholder="수정용 비밀번호를 입력하세요"
                required
              />
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 본문 영역 -->
      <div v-if="!isEditing" class="doc-body">
        {{ board.content }}
      </div>
      <div v-else class="doc-body edit-body">
        <label class="edit-label">내용</label>
        <textarea v-model="editForm.content" rows="10" class="edit-textarea" required></textarea>
      </div>

      <!-- 하단 액션 바 -->
      <footer class="doc-footer">
        <div>
          <NuxtLink to="/" class="btn btn-secondary">목록으로</NuxtLink>
        </div>
        <div class="btn-group">
          <template v-if="!isEditing">
            <button @click="startEdit" class="btn btn-primary">수정</button>
            <button @click="deleteBoard" class="btn btn-danger">삭제</button>
          </template>
          <template v-else>
            <button @click="cancelEdit" class="btn btn-secondary">취소</button>
            <button @click="submitEdit" class="btn btn-primary" :disabled="isSubmitting">
              {{ isSubmitting ? '저장 중...' : '저장 완료' }}
            </button>
          </template>
        </div>
      </footer>
    </article>

    <!-- 댓글 섹션 -->
    <section v-if="board" class="comments-section">
      <div class="comments-header">
        <h3>댓글 <span>({{ comments ? comments.length : 0 }})</span></h3>
      </div>

      <!-- 댓글 작성 폼 -->
      <form @submit.prevent="submitComment" class="comment-form-card">
        <div class="comment-form-inputs">
          <input
            v-model="commentForm.nickname"
            type="text"
            placeholder="닉네임"
            class="input-sm"
            required
          />
          <input
            v-model="commentForm.password"
            type="password"
            placeholder="비밀번호"
            class="input-sm"
            required
          />
        </div>
        <div class="comment-form-textarea">
          <textarea
            v-model="commentForm.content"
            rows="3"
            placeholder="댓글을 입력하세요..."
            required
          ></textarea>
          <button type="submit" class="btn btn-primary btn-comment" :disabled="isCommentSubmitting">
            {{ isCommentSubmitting ? '등록 중...' : '댓글 등록' }}
          </button>
        </div>
      </form>

      <!-- 댓글 목록 -->
      <div class="comment-list">
        <div v-if="commentsPending" class="comment-empty">댓글을 불러오는 중...</div>
        <div v-else-if="!comments || comments.length === 0" class="comment-empty">
          등록된 댓글이 없습니다. 첫 번째 댓글을 남겨보세요.
        </div>
        <div v-else v-for="item in comments" :key="item.id" class="comment-item">
          <div class="comment-meta">
            <div class="meta-left">
              <span class="comment-author">{{ item.nickname }}</span>
              <span class="comment-ip font-mono">{{ maskIp(item.ipAddress) }}</span>
              <span class="comment-date font-mono">{{ item.createdAt }}</span>
            </div>
            <button @click="deleteComment(item.id)" class="btn-del-comment">삭제</button>
          </div>
          <p class="comment-content">{{ item.content }}</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const route = useRoute()
const router = useRouter()
const boardId = route.params.id

// 1. 게시글 상세 조회
const { data: board, pending, error, refresh: refreshBoard } = await useFetch(`http://localhost:8080/api/boards/${boardId}`)

// 2. 댓글 목록 조회
const { data: comments, pending: commentsPending, refresh: refreshComments } = await useFetch(`http://localhost:8080/api/boards/${boardId}/comments`)

// 게시글 수정 상태 관리
const isEditing = ref(false)
const isSubmitting = ref(false)
const editForm = reactive({
  title: '',
  content: '',
  password: ''
})

const startEdit = () => {
  editForm.title = board.value.title
  editForm.content = board.value.content
  editForm.password = ''
  isEditing.value = true
}

const cancelEdit = () => {
  isEditing.value = false
}

const submitEdit = async () => {
  if (!editForm.title.trim() || !editForm.content.trim()) {
    alert('제목과 내용을 모두 입력해주세요.')
    return
  }
  if (!editForm.password) {
    alert('수정을 위해 비밀번호를 입력해주세요.')
    return
  }

  isSubmitting.value = true
  try {
    await $fetch(`http://localhost:8080/api/boards/${boardId}`, {
      method: 'PUT',
      body: {
        title: editForm.title,
        content: editForm.content,
        password: editForm.password
      }
    })
    alert('수정 완료되었습니다.')
    isEditing.value = false
    await refreshBoard()
  } catch (err) {
    alert('수정 실패: ' + (err.data?.message || '비밀번호를 확인해주세요.'))
  } finally {
    isSubmitting.value = false
  }
}

// IP 마스킹 함수
const maskIp = (ip) => {
  if (!ip) return ''
  if (ip === '0:0:0:0:0:0:0:1' || ip === '::1') return '(127.0)'
  const parts = ip.split('.')
  if (parts.length >= 2) return `(${parts[0]}.${parts[1]})`
  return `(${ip})`
}

// 게시글 삭제
const deleteBoard = async () => {
  const password = prompt('삭제 비밀번호를 입력해주세요:')
  if (!password) return

  try {
    await $fetch(`http://localhost:8080/api/boards/${boardId}`, {
      method: 'DELETE',
      body: { password }
    })
    alert('삭제되었습니다.')
    router.push('/')
  } catch (err) {
    alert('삭제 실패: ' + (err.data?.message || '비밀번호를 확인해주세요.'))
  }
}

// 댓글 작성 상태 관리
const isCommentSubmitting = ref(false)
const commentForm = reactive({
  nickname: '',
  password: '',
  content: ''
})

// 댓글 등록
const submitComment = async () => {
  if (!commentForm.nickname.trim() || !commentForm.password.trim() || !commentForm.content.trim()) {
    alert('닉네임, 비밀번호, 내용을 모두 작성해주세요.')
    return
  }

  isCommentSubmitting.value = true
  try {
    await $fetch(`http://localhost:8080/api/boards/${boardId}/comments`, {
      method: 'POST',
      body: {
        nickname: commentForm.nickname,
        password: commentForm.password,
        content: commentForm.content
      }
    })
    // 폼 초기화 및 목록 새로고침
    commentForm.content = ''
    commentForm.password = ''
    await refreshComments()
  } catch (err) {
    alert('댓글 등록 실패: ' + (err.data?.message || err.message))
  } finally {
    isCommentSubmitting.value = false
  }
}

// 댓글 삭제
const deleteComment = async (commentId) => {
  const password = prompt('댓글 삭제 비밀번호를 입력해주세요:')
  if (!password) return

  try {
    await $fetch(`http://localhost:8080/api/comments/${commentId}`, {
      method: 'DELETE',
      body: { password }
    })
    alert('댓글이 삭제되었습니다.')
    await refreshComments()
  } catch (err) {
    alert('삭제 실패: ' + (err.data?.message || '비밀번호가 일치하지 않습니다.'))
  }
}
</script>

<style scoped>
.container {
  max-width: 860px;
  margin: 30px auto;
  padding: 0 16px;
}

.nav-bar {
  margin-bottom: 16px;
}

.btn-back {
  font-size: 13px;
  color: #475569;
  text-decoration: none;
  font-weight: 500;
}

.btn-back:hover {
  color: #2563eb;
}

.doc-card {
  background: #ffffff;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.doc-header {
  padding: 20px 24px 16px;
  border-bottom: 1px solid #f1f5f9;
}

.badge {
  display: inline-block;
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 4px;
  background-color: #eff6ff;
  color: #2563eb;
  border: 1px solid #bfdbfe;
  margin-bottom: 8px;
}
.badge-edited {
  font-size: 13px;
  font-weight: normal;
  color: #94a3b8;
  margin-left: 6px;
  vertical-align: middle;
}

.doc-title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
}

.meta-table {
  width: 100%;
  border-collapse: collapse;
  background-color: #f8fafc;
  border-top: 1px solid #e2e8f0;
  border-bottom: 1px solid #e2e8f0;
  font-size: 13px;
}

.meta-table th {
  width: 15%;
  padding: 8px 16px;
  background-color: #f1f5f9;
  color: #475569;
  font-weight: 600;
  text-align: left;
  border-right: 1px solid #e2e8f0;
}

.meta-table td {
  width: 35%;
  padding: 8px 16px;
  color: #1e293b;
}

.author-name {
  font-weight: 500;
  color: #0f172a;
}

.author-ip {
  margin-left: 6px;
  font-size: 12px;
  color: #94a3b8;
}

.doc-body {
  padding: 24px;
  min-height: 220px;
  font-size: 14px;
  line-height: 1.7;
  white-space: pre-wrap;
  color: #334155;
  background-color: #ffffff;
}

.doc-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  background-color: #f8fafc;
  border-top: 1px solid #e2e8f0;
}

.btn-group {
  display: flex;
  gap: 8px;
}

.edit-input-group label,
.edit-label {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 6px;
}

.edit-input,
.edit-textarea {
  width: 100%;
  box-sizing: border-box;
  padding: 8px 12px;
  font-size: 14px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  outline: none;
  background-color: #fff;
}

.pw-input {
  max-width: 260px;
}

.edit-textarea {
  resize: vertical;
}

.edit-body {
  padding: 20px 24px;
}

.status-box {
  padding: 40px;
  text-align: center;
  font-size: 14px;
  color: #64748b;
  border: 1px dashed #cbd5e1;
  border-radius: 6px;
}

.status-box.error {
  color: #ef4444;
  border-color: #fca5a5;
  background-color: #fef2f2;
}

/* 댓글 스타일 */
.comments-section {
  margin-top: 32px;
}

.comments-header h3 {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 12px;
}

.comments-header h3 span {
  color: #2563eb;
  font-size: 14px;
}

.comment-form-card {
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  padding: 16px;
  margin-bottom: 20px;
}

.comment-form-inputs {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.input-sm {
  width: 160px;
  padding: 6px 10px;
  font-size: 13px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  outline: none;
}

.comment-form-textarea {
  display: flex;
  gap: 10px;
}

.comment-form-textarea textarea {
  flex: 1;
  padding: 8px 12px;
  font-size: 13px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  outline: none;
  resize: none;
}

.btn-comment {
  min-width: 90px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-item {
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  padding: 14px 16px;
}

.comment-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.meta-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.comment-author {
  font-weight: 600;
  color: #0f172a;
}

.comment-ip {
  color: #94a3b8;
  font-size: 12px;
}

.comment-date {
  color: #94a3b8;
  font-size: 12px;
}

.btn-del-comment {
  background: none;
  border: none;
  color: #94a3b8;
  font-size: 12px;
  cursor: pointer;
}

.btn-del-comment:hover {
  color: #ef4444;
  text-decoration: underline;
}

.comment-content {
  margin: 0;
  font-size: 13.5px;
  line-height: 1.6;
  color: #334155;
  white-space: pre-wrap;
}

.comment-empty {
  text-align: center;
  padding: 24px;
  color: #94a3b8;
  font-size: 13px;
  background-color: #f8fafc;
  border: 1px dashed #cbd5e1;
  border-radius: 6px;
}
</style>