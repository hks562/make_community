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

    <!-- 상세 문서 카드 -->
    <article v-else-if="board" class="doc-card">
      <header class="doc-header">
        <span class="badge">문서 #{{ board.id }}</span>
        <h1 class="doc-title">{{ board.title }}</h1>
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
        </tbody>
      </table>

      <!-- 본문 영역 -->
      <div class="doc-body">
        {{ board.content }}
      </div>

      <!-- 하단 액션 바 -->
      <footer class="doc-footer">
        <NuxtLink to="/" class="btn btn-secondary">목록으로</NuxtLink>
        <button @click="deleteBoard" class="btn btn-danger">삭제</button>
      </footer>
    </article>
  </div>
</template>

<script setup>
const route = useRoute()
const router = useRouter()
const boardId = route.params.id

const { data: board, pending, error } = await useFetch(`http://localhost:8080/api/boards/${boardId}`)

// IP 주소 마스킹 함수 (예: 192.168.0.1 -> (192.168))
const maskIp = (ip) => {
  if (!ip) return ''

  // 로컬 IPv6 루프백 주소 예외 처리
  if (ip === '0:0:0:0:0:0:0:1' || ip === '::1') {
    return '(127.0)'
  }

  // IPv4 형태
  const parts = ip.split('.')
  if (parts.length >= 2) {
    return `(${parts[0]}.${parts[1]})`
  }

  return `(${ip})`
}

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
  padding: 12px 24px;
  background-color: #f8fafc;
  border-top: 1px solid #e2e8f0;
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
</style>