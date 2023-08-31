import { useEffect, useState } from "react";
import { useRecoilState } from "recoil";
import { isReadStatus } from "@feature/Todo";

import { usePutTodos, useDeleteTodos } from "@api/todo/hook";

import Button from "@component/common/Button";
import Text from "@component/common/Text";
import { GetResDetailTodo } from "@type/todo/todo.res.dto";

type IReadOrUpdateTodo = {
    singleTodo?: GetResDetailTodo;
};

function ReadOrUpdateTodo({ singleTodo }: IReadOrUpdateTodo) {
    const [isRead, setIsRead] = useRecoilState(isReadStatus);
    const [value, setValue] = useState<string>(singleTodo?.todo || "");
    const [isCompleted, setIsCompleted] = useState<boolean>(!!singleTodo?.completed);

    const { mutate: putTodo } = usePutTodos({ todoId: singleTodo?.id || 0, completed: isCompleted, todo: value });
    const { mutate: deleteTodo } = useDeleteTodos({ todoId: singleTodo?.id || 0 });

    useEffect(() => {
        if (singleTodo?.todo) {
            setValue(singleTodo.todo);
        }

        if (singleTodo?.completed) {
            setIsCompleted(singleTodo.completed);
        }
    }, [singleTodo]);

    return (
        <div className="flex flex-1 flex-col p-24">
            <div className="mb-24 flex w-full justify-end">
                {isRead ? (
                    <>
                        <Button type="SUB" label="수정" isFullBtn={false} onClickHandler={() => setIsRead(false)} />
                        <Button type="WARN" label="삭제" isFullBtn={false} onClickHandler={() => deleteTodo()} />
                    </>
                ) : (
                    <Button
                        type="SUB"
                        label="완료"
                        isFullBtn={false}
                        onClickHandler={() => {
                            setIsRead(true);
                            putTodo();
                        }}
                    />
                )}
            </div>
            {singleTodo ? (
                <ul className="flex h-140 w-600 flex-col justify-between">
                    <li className="flex border-b-1 border-borderline py-8">
                        <Text type="Body" text="아이디" styles="w-100" />
                        <Text type="Body" text={singleTodo.id.toString()} />
                    </li>
                    <li className="flex border-b-1 border-borderline py-8">
                        <Text type="Body" text="유저 아이디" styles="w-100" />
                        <Text type="Body" text={singleTodo.userId.toString()} />
                    </li>
                    <li className="flex border-b-1 border-borderline py-8">
                        <Text type="Body" text="할일" styles="w-100" />
                        {isRead ? (
                            <Text type="Body" text={value} />
                        ) : (
                            <input
                                type="text"
                                value={value}
                                onChange={(e) => setValue(e.currentTarget.value)}
                                className="w-500 rounded border-1 border-borderline"
                            />
                        )}
                    </li>
                    <li className="flex border-b-1 border-borderline py-8">
                        <Text type="Body" text="완료 여부" styles="w-100" />
                        {isRead ? (
                            <Text type="Body" text={isCompleted ? "✅" : "❎"} />
                        ) : (
                            <input
                                type="checkbox"
                                checked={isCompleted}
                                onChange={() => setIsCompleted(!isCompleted)}
                            />
                        )}
                    </li>
                </ul>
            ) : null}
        </div>
    );
}

export default ReadOrUpdateTodo;
