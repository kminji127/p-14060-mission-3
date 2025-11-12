package com.back;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    // 입력 스캐너
    Scanner sc = new Scanner(System.in);
    // 등록 번호
    int id = 0;
    // 파일 경로
    Path basePath = Paths.get("db", "wiseSaying");
    Path idPath = Paths.get(String.valueOf(basePath), "lastId.txt");
    // 입력된 명언 리스트
    List<WiseSaying> wiseSayingList = new ArrayList<>();

    void run() {
        System.out.println("== 명언 앱 ==");
        init();

        // 종료하기 전까지 반복
        while (true) {
            // 명령어 입력
            System.out.print("명령) ");
            String cmd = sc.nextLine().trim(); // 양옆 공백 제거

            // 종료 조건
            if (cmd.equals("종료")) {
                break;
            } else if (cmd.equals("등록")) {
                createWiseSaying();
            } else if (cmd.equals("목록")) {
                System.out.println("번호 / 작가 / 명언");
                System.out.println("----------------------");
                for (int i = wiseSayingList.size() - 1; i >= 0; i--) {
                    WiseSaying wiseSaying = wiseSayingList.get(i);
                    System.out.println(wiseSaying.id + " / " + wiseSaying.author + " / " + wiseSaying.content);
                }
            } else if (cmd.startsWith("삭제?id=")) {
                // id 파악
                String[] cmdBits = cmd.split("=", 2);
                if (cmdBits.length < 2 || cmdBits[1].isEmpty()) {
                    System.out.println("id를 입력해주세요.");
                    continue; // 다시 반복문(명령어 입력)으로 돌아감
                }

                int targetId;

                try {
                    targetId = Integer.parseInt(cmdBits[1]);
                } catch (NumberFormatException e) {
                    System.out.println("id는 반드시 숫자여야 합니다.");
                    continue; // 다시 반복문(명령어 입력)으로 돌아감
                }

                boolean isDeleted = false;

                // 해당 id에 해당하는 리스트 원소가 있으면 삭제
                for (int i = wiseSayingList.size() - 1; i >= 0; i--) { // 삭제되면 리스트 뒤의 요소들이 앞으로 당겨지므로 뒤에서부터 순회
                    if (wiseSayingList.get(i).id == targetId) {
                        wiseSayingList.remove(i);

                        // 파일 삭제
                        Path wiseSayingPath = Paths.get(String.valueOf(basePath), "%d.json".formatted(targetId));
                        if (Files.exists(wiseSayingPath)) {
                            try {
                                Files.delete(wiseSayingPath);
                            } catch (Exception e) {
                                System.out.println("파일 삭제 실패" + e);
                            }
                        }
                        isDeleted = true;
                        break;
                    }
                }

                if (isDeleted) {
                    System.out.println(targetId + "번 명언이 삭제되었습니다.");
                } else {
                    // 리스트가 빈 값이라도 예외 처리
                    System.out.println(targetId + "번 명언은 존재하지 않습니다.");
                }
            } else if (cmd.startsWith("수정?id=")) {
                // 탐색 여부
                boolean isFound = false;
                // id 파악
                String[] cmdBits = cmd.split("=", 2);
                if (cmdBits.length < 2 || cmdBits[1].isEmpty()) {
                    System.out.println("id를 입력해주세요.");
                    continue; // 다시 반복문(명령어 입력)으로 돌아감
                }

                int targetId;

                try {
                    targetId = Integer.parseInt(cmdBits[1]);
                } catch (NumberFormatException e) {
                    System.out.println("id는 반드시 숫자여야 합니다.");
                    continue; // 다시 반복문(명령어 입력)으로 돌아감
                }

                // 해당 id에 해당하는 리스트 원소가 있으면 수정
                for (WiseSaying wiseSaying : wiseSayingList) {
                    if (wiseSaying.id == targetId) {
                        isFound = true;
                        System.out.println("명언(기존): " + wiseSaying.content);
                        System.out.print("명언 : ");
                        String newContent = sc.nextLine();
                        wiseSaying.content = newContent; // 객체 값 바꾸기

                        System.out.println("작가(기존): " + wiseSaying.author);
                        System.out.print("작가 : ");
                        String newAuthor = sc.nextLine();
                        wiseSaying.author = newAuthor; // 객체 값 바꾸기

                        // 파일 내용 바꾸기
                        Path wiseSayingPath = Paths.get(String.valueOf(basePath), "%d.json".formatted(targetId));
                        File wiseSayingFile = new File(wiseSayingPath.toString());
                        String json = "{\n  \"id\": %d,\n  \"content\": \"%s\",\n  \"author\": \"%s\"\n}".formatted(targetId, newContent, newAuthor);
                        try {
                            FileWriter fileWriter = new FileWriter(wiseSayingFile);
                            fileWriter.write(json);
                            fileWriter.close();
                        } catch (IOException e) {
                            System.out.println("파일 수정 실패" + e);
                        }
                        break;
                    }
                }
                if (!isFound) {
                    System.out.println(targetId + "번 명언은 존재하지 않습니다.");
                }
            } else if (cmd.equals("빌드")) {
                // 지금까지 저장된 명언을 json 배열로 저장
                Path jsonPath = Paths.get(String.valueOf(basePath), "data.json");
                File jsonFile = new File(jsonPath.toString());
                try {
                    FileWriter fileWriter = new FileWriter(jsonFile);
                    fileWriter.write("[\n");
                    for (int i = 0; i < wiseSayingList.size(); i++) {
                        WiseSaying wiseSaying = wiseSayingList.get(i);
                        String json = ("  {\n    \"id\": %d,\n    \"content\": \"%s\",\n    \"author\": \"%s\"\n  }").formatted(wiseSaying.id, wiseSaying.content, wiseSaying.author);
                        fileWriter.write(json);
                        // 마지막 요소에는 쉼표와 줄바꿈 하지 않음
                        if (i < wiseSayingList.size() - 1) {
                            fileWriter.write(",\n");
                        }
                    }
                    fileWriter.write("\n]");
                    fileWriter.close();
                    System.out.println("data.json 파일의 내용이 갱신되었습니다.");
                } catch (IOException e) {
                    System.out.println("파일 수정 실패" + e);
                }
            }
        }

        sc.close();
    }

    // 초기 설정
    void init() {
        // 마지막에 생성된 명언 번호 파일이 있고 유효하면 해당 숫자로 변경
        if (Files.exists(idPath)) {
            try {
                BufferedReader bufferedReader = Files.newBufferedReader(idPath);
                id = Integer.parseInt(bufferedReader.readLine());
                bufferedReader.close();
            } catch (IOException e) {
                System.out.println("ID 파일 읽기 실패" + e);
            } catch (NumberFormatException e) {
                System.out.println("정수 변환 실패(유효한 형식이 아님)" + e);
            }
        }

        // 시작 전 이미 저장된 명언은 리스트에 저장
        for (int i = 1; i <= id; i++) {
            Path wiseSayingPath = Paths.get(String.valueOf(basePath), "%d.json".formatted(i));
            if (Files.exists(wiseSayingPath)) {
                try {
                    BufferedReader bufferedReader = Files.newBufferedReader(wiseSayingPath);
                    bufferedReader.readLine(); // 중괄호만 있는 첫 줄 생략

                    String line;
                    line = bufferedReader.readLine();
                    int wiseSayingId = Integer.parseInt(line.substring(8, line.length() - 1));

                    line = bufferedReader.readLine();
                    String wiseSayingContent = line.substring(14, line.length() - 2);

                    line = bufferedReader.readLine();
                    String wiseSayingAuthor = line.substring(13, line.length() - 1);

                    wiseSayingList.add(new WiseSaying(wiseSayingId, wiseSayingContent, wiseSayingAuthor));

                    bufferedReader.close();
                } catch (IOException e) {
                    System.out.println("ID 파일 읽기 실패" + e);
                }
            }
        }
    }

    // 등록 (C)
    void createWiseSaying() {
        // 명언, 작가 입력
        System.out.print("명언 : ");
        String content = sc.nextLine().trim();

        System.out.print("작가 : ");
        String author = sc.nextLine().trim();

        // 등록
        id++;
        wiseSayingList.add(new WiseSaying(id, content, author));
        System.out.println(id + "번 명언이 등록되었습니다.");

        // 명언 정보를 json 파일로 저장
        Path wiseSayingPath = Paths.get(String.valueOf(basePath), "%d.json".formatted(id));
        File wiseSayingFile = new File(wiseSayingPath.toString());
        String json = "{\n  \"id\": %d,\n  \"content\": \"%s\",\n  \"author\": \"%s\"\n}".formatted(id, content, author);
        try {
            FileWriter fileWriter = new FileWriter(wiseSayingFile);
            fileWriter.write(json);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("파일 저장 실패" + e);
        }

        // 가장 마지막에 생성된 명언 번호 저장
        try {
            File idFile = new File(idPath.toString());
            FileWriter fileWriter = new FileWriter(idFile);
            fileWriter.write(Integer.toString(id));
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("파일 저장 실패" + e);
        }
    }
}
