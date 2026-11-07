/*
 * Copyright 2026 youngmonkeys.org
 *
 * Licensed under the ezyplatform, Version 1.0.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://youngmonkeys.org/licenses/ezyplatform-1.0.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.youngmonkeys.ezyplatform.signature;

import java.io.ByteArrayOutputStream;

public final class EcdsaSignatureFormats {

    private static final int DER_SEQUENCE = 0x30;
    private static final int DER_INTEGER = 0x02;

    private EcdsaSignatureFormats() {}

    public static byte[] derToConcatenated(
        byte[] der,
        int componentLength
    ) {
        int offset = 0;
        if (der.length < 8 || (der[offset++] & 0xFF) != DER_SEQUENCE) {
            throw new IllegalArgumentException("invalid DER signature");
        }
        int[] sequenceLength = readLength(der, offset);
        offset = sequenceLength[1];
        if (offset + sequenceLength[0] != der.length) {
            throw new IllegalArgumentException("invalid DER signature length");
        }
        byte[] answer = new byte[componentLength * 2];
        offset = readInteger(der, offset, answer, 0, componentLength);
        offset = readInteger(
            der,
            offset,
            answer,
            componentLength,
            componentLength
        );
        if (offset != der.length) {
            throw new IllegalArgumentException("invalid DER signature trailing data");
        }
        return answer;
    }

    public static byte[] concatenatedToDer(byte[] concatenated) {
        if (concatenated.length == 0 || concatenated.length % 2 != 0) {
            throw new IllegalArgumentException("invalid concatenated signature length");
        }
        int componentLength = concatenated.length / 2;
        byte[] r = toDerInteger(concatenated, 0, componentLength);
        byte[] s = toDerInteger(concatenated, componentLength, componentLength);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(DER_SEQUENCE);
        writeLength(output, r.length + s.length);
        output.write(r, 0, r.length);
        output.write(s, 0, s.length);
        return output.toByteArray();
    }

    private static int readInteger(
        byte[] der,
        int offset,
        byte[] target,
        int targetOffset,
        int componentLength
    ) {
        if (offset >= der.length || (der[offset++] & 0xFF) != DER_INTEGER) {
            throw new IllegalArgumentException("invalid DER integer");
        }
        int[] length = readLength(der, offset);
        int valueLength = length[0];
        offset = length[1];
        if (valueLength <= 0 || offset + valueLength > der.length) {
            throw new IllegalArgumentException("invalid DER integer length");
        }
        int start = offset;
        int end = offset + valueLength;
        while (start < end - 1 && der[start] == 0) {
            ++start;
        }
        int actualLength = end - start;
        if (actualLength > componentLength) {
            throw new IllegalArgumentException("DER integer is too large");
        }
        System.arraycopy(
            der,
            start,
            target,
            targetOffset + componentLength - actualLength,
            actualLength
        );
        return end;
    }

    private static int[] readLength(byte[] der, int offset) {
        if (offset >= der.length) {
            throw new IllegalArgumentException("invalid DER length");
        }
        int first = der[offset++] & 0xFF;
        if (first < 0x80) {
            return new int[] {first, offset};
        }
        int byteCount = first & 0x7F;
        if (byteCount == 0 || byteCount > 2 || offset + byteCount > der.length) {
            throw new IllegalArgumentException("invalid DER length");
        }
        int length = 0;
        for (int i = 0; i < byteCount; ++i) {
            length = (length << 8) | (der[offset++] & 0xFF);
        }
        return new int[] {length, offset};
    }

    private static byte[] toDerInteger(
        byte[] source,
        int offset,
        int length
    ) {
        int start = offset;
        int end = offset + length;
        while (start < end - 1 && source[start] == 0) {
            ++start;
        }
        boolean needPadding = (source[start] & 0x80) != 0;
        int valueLength = end - start + (needPadding ? 1 : 0);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(DER_INTEGER);
        writeLength(output, valueLength);
        if (needPadding) {
            output.write(0);
        }
        output.write(source, start, end - start);
        return output.toByteArray();
    }

    private static void writeLength(
        ByteArrayOutputStream output,
        int length
    ) {
        if (length < 0x80) {
            output.write(length);
        } else if (length <= 0xFF) {
            output.write(0x81);
            output.write(length);
        } else {
            output.write(0x82);
            output.write(length >>> 8);
            output.write(length & 0xFF);
        }
    }
}
