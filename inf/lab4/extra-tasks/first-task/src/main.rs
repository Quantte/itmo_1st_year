use std::ffi::{CStr, CString};
use std::os::raw::c_char;
use std::fs;

#[repr(C)]
pub struct CLesson {
    time: *mut c_char,
    subject: *mut c_char,
    room: *mut c_char,
    type_: *mut c_char,
    teacher: *mut c_char,
    next: *mut CLesson
}

#[repr(C)]
pub struct CDay {
    name: *mut c_char,
    lessons: *mut CLesson,
    next: *mut CDay
}

struct Lesson {
    time: String,
    subject: String,
    room: String,
    type_: String,
    teacher: String,
}

struct Day {
    name: String,
    lessons: Vec<Lesson>
}

unsafe extern "C" {
    fn parse_hcl_file(path: *const c_char) -> *mut CDay;
    fn free_schedule(root: *mut CDay);
}


fn cstr_opt(ptr: *mut c_char) -> String {
    if ptr.is_null() {
        String::new()
    } else {
        unsafe {
            CStr::from_ptr(ptr).to_string_lossy().into_owned()
        }
    }
}

fn convert_schedule(root: *mut CDay) -> Vec<Day> {
    unsafe {
        let mut result = Vec::new();
        let mut d = root;
        while !d.is_null() {
            let cd = &*d;
            let mut lessons = Vec::new();
            let mut l = cd.lessons;

            while !l.is_null() {

                let cl = &*l;
                lessons.push(
                    Lesson {
                        time: cstr_opt(cl.time),
                        subject: cstr_opt(cl.subject),
                        room: cstr_opt(cl.room),
                        type_: cstr_opt(cl.type_),
                        teacher: cstr_opt(cl.teacher)
                    }
                );
                l = cl.next;
            }
            result.push(
                Day {
                    name: cstr_opt(cd.name),
                    lessons
                }
            );
            d = cd.next;
        }
        result
    }
}

fn dump_yaml(schedule: &[Day]) {
    let mut content = String::from("day:\n");
    for day in schedule {
        content.push_str(&format!("  - {}:\n    - lesson:\n", &day.name));
        for lesson in &day.lessons {
            content.push_str(&format!("      - time: \"{}\"\n", &lesson.time));
            content.push_str(&format!("        subject: \"{}\"\n", &lesson.subject));
            content.push_str(&format!("        room: \"{}\"\n", &lesson.room));
            content.push_str(&format!("        type: \"{}\"\n", &lesson.type_));
            content.push_str(&format!("        teacher: \"{}\"\n", &lesson.teacher));
        }
    }
    
    println!("{content}");

    match fs::write("schedule.yaml", content) {
        Ok(()) => print!("File \"schedule.yaml\" saved"),
        Err(e) => panic!("Error saving file: {}", e)
    }
}


fn main() {
    let path = std::env::args().nth(1).unwrap_or_else(|| "schedule.hcl".to_string());
    let c_path = CString::new(path).unwrap();

    unsafe {
        let root = parse_hcl_file(c_path.as_ptr());
        if root.is_null() {
            eprint!("Parsing error!");
            return;
        }
        let schedule = convert_schedule(root);
        dump_yaml(&schedule);
        free_schedule(root);
    }
}
