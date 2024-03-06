import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SearchService } from '../../services/search.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent implements OnInit{
  
  //form
  searchForm!: FormGroup;
  constructor (private fb: FormBuilder){}

  private searchSvc = inject(SearchService);

  ngOnInit(): void {
    this.searchForm=this.fb.group({
      query: this.fb.control<string>('', [Validators.required])
    })
  }

  search(event: Event){
    event.preventDefault(); 
    console.log("search query: ", this.searchForm.value.query);
    if (this.searchForm.valid){
      this.searchSvc.querySearch(this.searchForm.value.query);
    }
    
  }
}
