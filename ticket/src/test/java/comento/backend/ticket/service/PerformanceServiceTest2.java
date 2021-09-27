package comento.backend.ticket.service;

import comento.backend.ticket.config.customException.NotFoundDataException;
import comento.backend.ticket.domain.Performance;
import comento.backend.ticket.dto.PerformanceDto;
import comento.backend.ticket.dto.PerformanceResponse;
import comento.backend.ticket.repository.PerformanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times; //몇 번 호출되었는지 검증
import static org.mockito.Mockito.verify; //해당 기능이 사용되었는지 검증

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class) //JUnit5와 Mockito 연동
public class PerformanceServiceTest2 {

    @Mock //mock객체로 생성
    private PerformanceRepository performanceRepository;

    private PerformanceService performanceService;

    @BeforeEach
    void init() {
        //실제 존재하는 performanceRepository가 주입되는것이 아닌 Mock PerformanceRepository가 주입
        performanceService = new PerformanceService(performanceRepository);
    }
    @Test
    @DisplayName("[실패] 없는 날짜 정보 입력 - 404 NOT FOUND ERROR")
    void 공연정보가_없으면_예외를_던진다2() throws ParseException {
        //given
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = format.parse("2021-06-31");

        //performanceRepository를 mock했으니 테스트 상황에서 원하는 결과를 새롭게 구현하는 코드.
        //테스트가 돌아가는 동안에는 willReturn에서 반환하는 결과를 반드시 return하므로 PerformanceRepository를 의존하지 않고도 돌아가는 테스트코드가 완성
        given(performanceRepository.findByStartDateGreaterThanEqualOrderByStartDateAsc(startDate))
                .willReturn(new ArrayList<Performance>());
        //when
        //공연정보가 없는 경우 Service에서는 NOT FOUND ERROR 예외 호출
        NotFoundDataException nfde = assertThrows(NotFoundDataException.class, () -> {
            performanceService.getListPerformance(new PerformanceDto(null, startDate));
        });
        //then
        String msg = nfde.getMessage();
        assertThat(msg).isEqualTo(null);
    }
    //@TODO : 성공해야 하는데 두 테스트 메소드 모두 NotFoundDataException 발생,,
    @Test
    @DisplayName("[성공] 날짜를 정확히 입력한 경우")
    void 공연정보가_있으면_응답한다1() throws ParseException {
        //given
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = format.parse("2021-06-20");

        given(performanceRepository.findByStartDateGreaterThanEqualOrderByStartDateAsc(startDate))
                .willReturn(new ArrayList<>());
        //when
        List<PerformanceResponse> result =  performanceService.getListPerformance(new PerformanceDto(null, startDate));

        //then
        assertThat(result.size()).isNotZero(); //2개
    }
    @Test
    @DisplayName("[성공] 날짜, 제목을 정확히 입력한 경우")
    void 공연정보가_있으면_응답한다2() throws ParseException {
        //given
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = format.parse("2021-06-20");
        String title = "국립무용단 <산조>";
        List<Performance> temp = new ArrayList<>();
        given(performanceRepository.findByTitleAndStartDateGreaterThanEqualOrderByStartDate(title, startDate))
                .willReturn(temp);
        //when
        List<PerformanceResponse> result = performanceService.getListPerformance(new PerformanceDto(title, startDate));

        //then
        assertThat(result.size()).isNotZero();
    }
}