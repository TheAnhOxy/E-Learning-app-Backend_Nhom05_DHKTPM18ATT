package com.elearning.converter;

import com.elearning.entity.Answer;
import com.elearning.entity.Lesson;
import com.elearning.entity.Question;
import com.elearning.entity.Quiz;
import com.elearning.exception.ResourceNotFoundException;
import com.elearning.modal.dto.request.AnswerRequestDTO;
import com.elearning.modal.dto.request.QuestionRequestDTO;
import com.elearning.modal.dto.request.QuizRequestDTO;
import com.elearning.modal.dto.response.AnswerResponseDTO;
import com.elearning.modal.dto.response.QuestionResponseDTO;
import com.elearning.modal.dto.response.QuizResponseDTO;
import com.elearning.repository.AnswerRepository;
import com.elearning.repository.LessonRepository;
import com.elearning.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class QuizConverter {

    private final ModelMapper modelMapper;
    private final LessonRepository lessonRepository;

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;


    @Transactional
    public Quiz toEntity(QuizRequestDTO dto) {
        Quiz quiz = new Quiz();
        quiz.setTitle(dto.getTitle());

        Lesson lesson = lessonRepository.findById(dto.getLessonId())
                .orElseThrow();
        quiz.setLesson(lesson);

        Set<Question> questions = new HashSet<>();
        for (QuestionRequestDTO qDto : dto.getQuestions()) {
            Question question = new Question();
            question.setQuestionText(qDto.getQuestionText());
            question.setQuestionType(qDto.getQuestionType());
            question.setQuiz(quiz);
            Set<Answer> answers = new HashSet<>();
            for (AnswerRequestDTO aDto : qDto.getAnswers()) {
                Answer answer = new Answer();
                answer.setAnswerText(aDto.getAnswerText());
                answer.setIsCorrect(aDto.getIsCorrect());
                answer.setQuestion(question);
                answers.add(answer);
            }
            question.setAnswers(answers);
            questions.add(question);
        }
        quiz.setQuestions(questions);

        return quiz;
    }


    @Transactional
    public void updateEntity(Quiz existingQuiz, QuizRequestDTO dto) {
        existingQuiz.setTitle(dto.getTitle());
        if (!existingQuiz.getLesson().getId().equals(dto.getLessonId())) {
            Lesson lesson = lessonRepository.findById(dto.getLessonId())
                    .orElseThrow();
            existingQuiz.setLesson(lesson);
        }
        updateQuestions(existingQuiz, dto.getQuestions());
    }

    private void updateQuestions(Quiz quiz, List<QuestionRequestDTO> qDtos) {
        Set<Integer> dtoQuestionIds = qDtos.stream()
                .map(QuestionRequestDTO::getId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        Set<Question> questionsToDelete = quiz.getQuestions().stream()
                .filter(q -> !dtoQuestionIds.contains(q.getId()))
                .collect(Collectors.toSet());
        questionRepository.deleteAll(questionsToDelete);
        quiz.getQuestions().removeAll(questionsToDelete);
        for (QuestionRequestDTO qDto : qDtos) {
            Question question;
            if (qDto.getId() != null) {
                question = quiz.getQuestions().stream()
                        .filter(q -> q.getId().equals(qDto.getId()))
                        .findFirst()
                        .orElseThrow();

                question.setQuestionText(qDto.getQuestionText());
                question.setQuestionType(qDto.getQuestionType());
            } else {
                question = new Question();
                question.setQuestionText(qDto.getQuestionText());
                question.setQuestionType(qDto.getQuestionType());
                question.setQuiz(quiz);
            }
            updateAnswers(question, qDto.getAnswers());

            if(qDto.getId() == null) {
                quiz.getQuestions().add(question);
            }
        }
    }

    private void updateAnswers(Question question, List<AnswerRequestDTO> aDtos) {
        Set<Integer> dtoAnswerIds = aDtos.stream()
                .map(AnswerRequestDTO::getId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Set<Answer> answersToDelete = question.getAnswers().stream()
                .filter(a -> !dtoAnswerIds.contains(a.getId()))
                .collect(Collectors.toSet());

        answerRepository.deleteAll(answersToDelete);
        question.getAnswers().removeAll(answersToDelete);

        for (AnswerRequestDTO aDto : aDtos) {
            Answer answer;
            if (aDto.getId() != null) {
                answer = question.getAnswers().stream()
                        .filter(a -> a.getId().equals(aDto.getId()))
                        .findFirst()
                        .orElseThrow();

                answer.setAnswerText(aDto.getAnswerText());
                answer.setIsCorrect(aDto.getIsCorrect());
            } else {
                answer = new Answer();
                answer.setAnswerText(aDto.getAnswerText());
                answer.setIsCorrect(aDto.getIsCorrect());
                answer.setQuestion(question);
                question.getAnswers().add(answer);
            }
        }
    }


    public QuizResponseDTO toDTO(Quiz entity) {
        QuizResponseDTO dto = modelMapper.map(entity, QuizResponseDTO.class);

        if (entity.getLesson() != null) {
            dto.setLessonId(entity.getLesson().getId());
        }
        if (entity.getQuestions() != null) {
            dto.setQuestions(entity.getQuestions().stream()
                    .map(this::toQuestionDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private QuestionResponseDTO toQuestionDTO(Question qEntity) {
        QuestionResponseDTO qDto = modelMapper.map(qEntity, QuestionResponseDTO.class);
        if (qEntity.getAnswers() != null) {
            qDto.setAnswers(qEntity.getAnswers().stream()
                    .map(this::toAnswerDTO)
                    .collect(Collectors.toList()));
        }
        return qDto;
    }
    private AnswerResponseDTO toAnswerDTO(Answer aEntity) {
        return modelMapper.map(aEntity, AnswerResponseDTO.class);
    }
}