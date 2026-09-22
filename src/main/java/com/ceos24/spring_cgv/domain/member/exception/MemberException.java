package com.ceos24.spring_cgv.domain.member.exception;

import com.ceos24.spring_cgv.global.apiPayload.code.BaseErrorCode;
import com.ceos24.spring_cgv.global.apiPayload.exception.ProjectException;

public class MemberException extends ProjectException {
    public MemberException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
