interface QuestionDefaultType {
    title: string;
    content: string;
}

// 질문 게시판 - 리스트 조회
export interface GetReqAllQuestion {
    search?: string;
}

// 질문 게시판 - 등록
export interface PostReqQuestion extends QuestionDefaultType {}

// 질문 게시판 - 수정 (본인이 작성한 글만 가능)
export interface PatchReqQuestion extends QuestionDefaultType {
    questionId: number;
}

// 질문 게시판 - 삭제 (본인이 작성한 글만 가능)
export interface DeleteReqQuestion {
    questionId: number;
}
