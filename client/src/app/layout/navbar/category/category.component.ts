import { Component, inject, OnInit } from '@angular/core';
import { CategoryService } from './category.service';
import { Category } from './category.model';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';

@Component({
  selector: 'app-category',
  standalone: true,
  imports: [FaIconComponent],
  templateUrl: './category.component.html',
  styleUrl: './category.component.scss',
})
export class CategoryComponent implements OnInit {
  categoryService: CategoryService = inject(CategoryService);
  categories: Category[] | undefined;
  currentActiveCategory = this.categoryService.getCategoryByDefault();

  ngOnInit(): void {
    this.categories = this.categoryService.getCategories();
  }
}
