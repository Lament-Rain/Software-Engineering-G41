from pathlib import Path

path = Path(r"c:\Users\Lenovo\Desktop\MiniProject\Software-Engineering-G41\miniproject v1\src\service\DataStorage.java")
text = path.read_text(encoding="utf-8")
old = """    // 加载申请数据
    private static void loadApplications() {
        try (BufferedReader reader = new BufferedReader(new FileReader(APPLICATIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 简化处理
            }
        } catch (FileNotFoundException e) {
            // 文件不存在，创建空列表
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
"""
new = """    // 加载申请数据
    private static void loadApplications() {
        try (BufferedReader reader = new BufferedReader(new FileReader(APPLICATIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(\"Application{\")) {
                    continue;
                }

                try {
                    Application app = new Application();

                    if (line.contains(\"id='\")) {
                        String id = line.split(\"id='\")[1].split(\"'\")[0];
                        app.setId(id);
                    }
                    if (line.contains(\"taId='\")) {
                        String taId = line.split(\"taId='\")[1].split(\"'\")[0];
                        app.setTaId(taId);
                    }
                    if (line.contains(\"jobId='\")) {
                        String jobId = line.split(\"jobId='\")[1].split(\"'\")[0];
                        app.setJobId(jobId);
                    }
                    if (line.contains(\"coverLetter='\")) {
                        String coverLetter = line.split(\"coverLetter='\")[1].split(\"'\")[0];
                        app.setCoverLetter(coverLetter);
                    }
                    if (line.contains(\"status=\")) {
                        String statusStr = line.split(\"status=\")[1].split(\",\")[0].replaceAll(\"[^A-Z_]\", \"\");
                        try {
                            app.setStatus(model.ApplicationStatus.valueOf(statusStr));
                        } catch (IllegalArgumentException e) {
                            app.setStatus(model.ApplicationStatus.PENDING);
                        }
                    }
                    if (line.contains(\"createdAt='\")) {
                        String createdAt = line.split(\"createdAt='\")[1].split(\"'\")[0];
                        app.setCreatedAt(createdAt);
                    }
                    if (line.contains(\"updatedAt='\")) {
                        String updatedAt = line.split(\"updatedAt='\")[1].split(\"'\")[0];
                        app.setUpdatedAt(updatedAt);
                    }
                    if (line.contains(\"reviewedBy='\")) {
                        String reviewedBy = line.split(\"reviewedBy='\")[1].split(\"'\")[0];
                        app.setReviewedBy(\"null\".equals(reviewedBy) ? null : reviewedBy);
                    }
                    if (line.contains(\"reviewTime='\")) {
                        String reviewTime = line.split(\"reviewTime='\")[1].split(\"'\")[0];
                        app.setReviewTime(\"null\".equals(reviewTime) ? null : reviewTime);
                    }
                    if (line.contains(\"reviewComment='\")) {
                        String reviewComment = line.split(\"reviewComment='\")[1].split(\"'\")[0];
                        app.setReviewComment(\"null\".equals(reviewComment) ? null : reviewComment);
                    }
                    if (line.contains(\"matchScore=\")) {
                        String matchScoreStr = line.split(\"matchScore=\")[1].split(\"}\")[0].trim();
                        try {
                            app.setMatchScore(Double.parseDouble(matchScoreStr));
                        } catch (NumberFormatException e) {
                            app.setMatchScore(0.0);
                        }
                    }

                    applications.add(app);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            // 文件不存在，创建空列表
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
"""
if old not in text:
    raise SystemExit("old block not found")
path.write_text(text.replace(old, new), encoding="utf-8")
