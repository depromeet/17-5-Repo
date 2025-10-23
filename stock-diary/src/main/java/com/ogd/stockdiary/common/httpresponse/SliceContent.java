package com.ogd.stockdiary.common.httpresponse;

import java.util.List;

public record SliceContent<T>(List<T> content, String nextCursor) {
}
