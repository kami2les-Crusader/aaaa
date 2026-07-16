package jp.levtech.rookie.tutorial.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.levtech.rookie.tutorial.model.Todo;

@Controller
public class TodoController {

	private final List<Todo> todoList = new ArrayList<>();
	private int nextId = 1; // IDを採番するカウンター
	//カレンダー画面

	@GetMapping("/")
	public String calender(
			@RequestParam(defaultValue = "0") int year,
			@RequestParam(defaultValue = "0") int month,
			Model model) {

		LocalDate today = LocalDate.now();
		if (year == 0)
			year = today.getYear();
		if (month == 0)
			month = today.getMonthValue();

		YearMonth current = YearMonth.of(year, month);
		YearMonth prev = current.minusMonths(1);
		YearMonth next = current.plusMonths(1);

		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("prevYear", prev.getYear());
		model.addAttribute("prevMonth", prev.getMonthValue());
		model.addAttribute("nextYear", next.getYear());
		model.addAttribute("nextMonth", next.getMonthValue());
		model.addAttribute("weeks", buildCalendar(year, month));

		String todayStr = String.format("%04d-%02d-%02d",
				today.getYear(), today.getMonthValue(), today.getDayOfMonth());
		List<Todo> todayTodos = todoList.stream()
				.filter(t -> t.getDate().equals(todayStr))
				.collect(Collectors.toList());
		model.addAttribute("todayStr", todayStr);
		model.addAttribute("todayTodos", todayTodos);

		return "Calender";
	}

	//todo画面
	@GetMapping("/todo")
	public String todo(@RequestParam String date, Model model) {
		List<Todo> todosForDate = todoList.stream()
				.filter(t -> t.getDate().equals(date))
				.collect(Collectors.toList());

		model.addAttribute("date", date);
		model.addAttribute("todos", todosForDate);
		return "todo";
	}

	//新規作成画面
	@GetMapping("/todo/new")
	public String newTodo(@RequestParam String date, Model model) {
		model.addAttribute("date", date);
		return "create";
	}

	@PostMapping("/todo/create")
	public String createTodo(@RequestParam String date,
			@RequestParam String title,
			@RequestParam String memo) {
		todoList.add(new Todo(nextId++, date, title, memo)); // IDを採番して追加
		return "redirect:/todo?date=" + date;
	}

	// 詳細画面 
	@GetMapping("/todo/detail")
	public String detail(@RequestParam int id, Model model) {
		Todo todo = todoList.stream()
				.filter(t -> t.getId() == id)
				.findFirst()
				.orElse(null);

		model.addAttribute("todo", todo);
		return "detail";
	}

	// 削除画面
	@GetMapping("/todo/delete-confirm")
	public String deleteConfirm(@RequestParam int id, Model model) {
	    Todo todo = todoList.stream()
	            .filter(t -> t.getId() == id)
	            .findFirst()
	            .orElse(null);

	    model.addAttribute("todo", todo);
	    return "delete-confirm";
	}
	private List<List<String>> buildCalendar(int year, int month) {
		YearMonth ym = YearMonth.of(year, month);
		LocalDate firstDay = ym.atDay(1);
		int startDow = firstDay.getDayOfWeek().getValue() % 7;

		List<List<String>> weeks = new ArrayList<>();
		List<String> week = new ArrayList<>();

		for (int i = 0; i < startDow; i++)
			week.add(null);

		for (int d = 1; d <= ym.lengthOfMonth(); d++) {
			week.add(String.format("%04d-%02d-%02d", year, month, d));
			if (week.size() == 7) {
				weeks.add(week);
				week = new ArrayList<>();
			}
		}

		while (week.size() < 7)
			week.add(null);
		if (!week.isEmpty())
			weeks.add(week);

		return weeks;
	}
}